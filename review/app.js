"use strict";

/* =================================================================
 * LeetCode 艾宾浩斯复习 · 云端版逻辑
 * 数据存 Supabase；页面每次打开实时计算排期。
 *
 * 核心规则：
 *   1. 到期复习题按「到期日」排序，每天只安排最多 DAILY_CAP 道作为「今日任务」；
 *   2. 排不上的到期题进入「排队区」，第二天自动顶上（即顺延），不会丢；
 *   3. 任何进行中的题都可以提前复习（题目库 / 日历里点「现在做」）；
 *   4. 点「完成」→ 轮次 +1 → 按艾宾浩斯间隔计算下次复习日期；攒满通关；
 *   5. 日历：绿=当天做过题数（从 history 统计），蓝=当天有排期，
 *      黄(仅今天)=排队中；点日期看当天详情；
 *   6. 统计：做题次数 / 不同题目 / 新学 / 通关，可按 周 / 月 / 年 / 累计；
 *   7. 「打开原题」无需 AI：题目名是 LeetCode 官方驼峰名时直接拼 slug；
 *      带题号时尝试用 LeetCode 公开接口缓存「题号→slug」映射；其余跳站内搜索。
 * ================================================================= */

const CFG = window.APP_CONFIG || {};
const ACCESS_CODE = (CFG.accessCode || "cwz");   // 打开网页需输入的访问口令（config.js 可改）
let SB = null;
const SUPABASE_CDN = "https://unpkg.com/@supabase/supabase-js@2/dist/umd/supabase.js";

const DAILY_CAP = 3;                                // 每天最多安排几道复习
const PREVIEW_DAYS = 7;                             // 「未来预告」看几天
const LEETCODE_BASE = (CFG.leetcodeBase || "https://leetcode.cn").replace(/\/+$/, "");
const SLUG_LC_KEY = "lc_slugmap_v1";                // 题号→slug 映射缓存在 localStorage

function loadSupabaseLib() {
  return new Promise((resolve, reject) => {
    if (window.supabase && typeof window.supabase.createClient === "function") return resolve();
    const s = document.createElement("script");
    s.src = SUPABASE_CDN;
    s.onload = resolve;
    s.onerror = () => reject(new Error("Supabase 客户端库加载失败：浏览器访问不到 " + SUPABASE_CDN));
    document.head.appendChild(s);
  });
}

function ensureClient() {
  if (SB) return Promise.resolve(SB);
  if (!CFG.supabaseUrl || CFG.supabaseUrl.includes("YOUR-") ||
      !CFG.supabaseAnonKey || CFG.supabaseAnonKey.includes("YOUR-")) {
    return Promise.reject(new Error("config.js 尚未填入 Supabase 地址与 anon key"));
  }
  return loadSupabaseLib().then(() => {
    SB = window.supabase.createClient(CFG.supabaseUrl, CFG.supabaseAnonKey);
    return SB;
  });
}

/* ---------------- 工具 ---------------- */
function esc(s) {
  return String(s == null ? "" : s)
    .replace(/&/g, "&amp;").replace(/</g, "&lt;")
    .replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
function $(sel) { return document.querySelector(sel); }
function removeFrom(arr, it) { const i = arr.indexOf(it); if (i >= 0) arr.splice(i, 1); }

let toastTimer = null;
function toast(msg, isErr) {
  const t = $("#toast");
  t.textContent = msg;
  t.classList.toggle("err", !!isErr);
  t.classList.add("show");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => t.classList.remove("show"), 3200);
}

/* ---------------- 日期 ---------------- */
const WEEK = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
const WEEK_SHORT = ["日", "一", "二", "三", "四", "五", "六"];
function pad2(n) { return (n < 10 ? "0" : "") + n; }
function isoOf(d) { return d.getFullYear() + "-" + pad2(d.getMonth() + 1) + "-" + pad2(d.getDate()); }
function todayISO() { return isoOf(new Date()); }
function addDaysISO(iso, n) {
  const p = iso.split("-").map(Number);
  const d = new Date(p[0], p[1] - 1, p[2] + n);
  return isoOf(d);
}
function fmtDate(iso) {
  const d = new Date(iso + "T00:00:00");
  return d.getFullYear() + " 年 " + (d.getMonth() + 1) + " 月 " + d.getDate() + " 日 · " + WEEK[d.getDay()];
}
function shortDate(iso) {
  const p = iso.split("-");
  return (p[1] | 0) + "月" + (p[2] | 0) + "日";
}
function fmtDow(iso) {
  const d = new Date(iso + "T00:00:00");
  return WEEK[d.getDay()].replace("周", "周");
}
function daysLate(a, b) {          // b - a 相差天数
  const pa = a.split("-").map(Number), pb = b.split("-").map(Number);
  return Math.round((new Date(pb[0], pb[1] - 1, pb[2]) - new Date(pa[0], pa[1] - 1, pa[2])) / 86400000);
}

function levelClass(lv) {
  const m = String(lv).toLowerCase();
  if (m.includes("easy") || m.includes("简单")) return "easy";
  if (m.includes("medium") || m.includes("中")) return "medium";
  if (m.includes("hard") || m.includes("难")) return "hard";
  return "q";
}
function titleOf(p) {
  const id = (p && p.id != null && p.id !== "") ? String(p.id) : "";
  if (id && String(p.key || "").indexOf(id) !== 0) return id + ". " + (p.name || "");
  return (p && p.name) ? p.name : String(p.key || "");
}
function levelSpan(p) {
  const lv = p.level || "?";
  return '<span class="level ' + levelClass(lv) + '">' + esc(lv) + "</span>";
}

/* ---------------- LeetCode 链接辅助（无需 AI） ---------------- */
// 把题目名转 LeetCode slug：
//   "TwoCityScheduling1029" → "two-city-scheduling"（驼峰）
//   "Squares of a Sorted Array" → "squares-of-a-sorted-array"（空格标题）
//   无法识别的名字（中文/乱名）返回空串，调用方会改用站内搜索。
function slugifyName(name) {
  let s = String(name || "").trim();
  s = s.replace(/^[\d.、:：#\-_\s]+/, "");      // 去掉开头的题号/标点/空格
  s = s.replace(/\d+$/, "");                    // 去掉结尾的题号
  s = s.replace(/[\s_]+/g, " ").trim();
  if (!s) return "";
  if (/[^A-Za-z0-9 \-]/.test(s)) return "";
  if (s.indexOf(" ") >= 0) {
    return s.toLowerCase().replace(/\s+/g, "-");
  }
  if (!/^[A-Za-z][A-Za-z0-9]*$/.test(s)) return "";
  return s.replace(/([a-z0-9])([A-Z])/g, "$1-$2")
          .replace(/([A-Z]+)([A-Z][a-z])/g, "$1-$2")
          .toLowerCase();
}

let slugCache = null;   // { 显示题号(string): titleSlug }
function loadSlugCache() {
  try { slugCache = JSON.parse(localStorage.getItem(SLUG_LC_KEY) || "null"); } catch (e) { slugCache = null; }
}
async function ensureSlugCache() {
  if (slugCache) return;
  try {
    const ctrl = new AbortController();
    const timer = setTimeout(() => ctrl.abort(), 8000);
    const res = await fetch(LEETCODE_BASE + "/api/problems/all/", { signal: ctrl.signal });
    clearTimeout(timer);
    if (!res.ok) throw new Error("HTTP " + res.status);
    const j = await res.json();
    const map = {};
    (j.stat_status_pairs || []).forEach(it => {
      const fid = it.stat && it.stat.frontend_question_id;
      const sl = it.stat && it.stat.question__title_slug;
      if (fid != null && sl) map[String(fid)] = sl;
    });
    slugCache = map;
    try { localStorage.setItem(SLUG_LC_KEY, JSON.stringify(map)); } catch (e) { /* 隐私模式忽略 */ }
  } catch (e) {
    slugCache = {};   // 拿不到就置空，避免本次会话反复请求
    try { localStorage.setItem(SLUG_LC_KEY, "{}"); } catch (e2) { /* 忽略 */ }
  }
}
function probByKey(key) { return PROBLEMS.find(p => p.key === key); }
function numOf(p) {
  if (p && p.id != null && p.id !== "") return String(p.id);
  const m = String((p && (p.name || p.key)) || "").match(/\d+/g);
  return m ? m[m.length - 1] : "";
}
function searchUrlOf(p) {
  const q = numOf(p) || (p && (p.name || p.key)) || "";
  return LEETCODE_BASE + "/problemset/?search=" + encodeURIComponent(q);
}
async function doOpen(p, btn) {
  if (!p) return;
  const id = numOf(p);
  const slug = slugifyName(p.name || p.key);
  let url = null;
  // 名字无法转 slug（如中文名）且带题号时，才尝试查「题号→slug」映射；
  // 能直接生成 slug 就跳过网络请求，避免多余的 CORS 报错。
  if (id && !slug) {
    if (btn) btn.textContent = "查映射…";
    await ensureSlugCache();
    const sl = slugCache ? slugCache[id] : null;
    if (sl) url = LEETCODE_BASE + "/problems/" + sl + "/";
  }
  if (!url && slug) url = LEETCODE_BASE + "/problems/" + slug + "/";
  if (!url) url = searchUrlOf(p);
  window.open(url, "_blank");
  if (btn) btn.textContent = "打开原题";
}

/* ---------------- 数据与状态 ---------------- */
let PROBLEMS = [];                       // 云端 problems 表全部行
let INTERVAL = Array.isArray(CFG.interval) ? CFG.interval : [1, 2, 4, 7, 15, 30, 60];
let DATA = null;                         // buildData() 的渲染快照
let BUSY = false;
let CAL = null;                          // 日历当前年月 { y, m }
let STAT_PERIOD = "month";               // week / month / year / all
let dayOpen = null;                      // 当前打开的日历日期
let libQ = "", libLv = "", libSt = "";

async function ensureMeta() {
  const { data } = await SB.from("meta").select("interval").eq("id", 1).maybeSingle();
  if (data && data.interval) { INTERVAL = data.interval; return; }
  const { error } = await SB.from("meta").insert({ id: 1, interval: INTERVAL });
  if (error && error.code !== "23505") throw error;
  const again = await SB.from("meta").select("interval").eq("id", 1).maybeSingle();
  if (again.data && again.data.interval) INTERVAL = again.data.interval;
}

/* ---------------- 排期计算 ---------------- */
function buildData() {
  const date = todayISO();
  const due = [], doneToday = [], done = [], active = [];
  for (const p of PROBLEMS) {
    if (p.completed) { done.push(p); continue; }
    active.push(p);
    if (p.last_done === date) { doneToday.push(p); continue; }
    if ((p.next_review || "9999") <= date) {
      due.push({ p, round: p.stage + 1, late: daysLate(p.next_review, date) });
    }
  }
  due.sort((a, b) =>
    (a.p.next_review < b.p.next_review ? -1 : a.p.next_review > b.p.next_review ? 1 : (a.p.key < b.p.key ? -1 : 1)));
  const todo = due.slice(0, DAILY_CAP);
  const queue = due.slice(DAILY_CAP);
  doneToday.sort((a, b) => (a.key < b.key ? -1 : 1));

  const allHist = PROBLEMS.reduce((n, p) => n + ((p.history || []).length), 0);
  return {
    ok: true, today: date, interval: INTERVAL,
    todo: todo, queue: queue, done_today: doneToday,
    future: buildPreview(PREVIEW_DAYS),
    active: active, done: done,
    todoKeys: todo.map(x => x.p.key),
    queueKeys: queue.map(x => x.p.key),
    allHist: allHist,
    stats: { active: active.length, done: done.length, total: PROBLEMS.length },
  };
}

// 模拟未来排期：今天排上的 3 道假设完成，排队/新到期每天最多安排 DAILY_CAP 道。
function buildPreview(days) {
  const today = todayISO();
  const total = INTERVAL.length;
  const sims = PROBLEMS.filter(p => !p.completed).map(p => ({
    key: p.key, id: p.id, name: p.name, level: p.level || "?", stage: p.stage,
    next_review: p.next_review || "9999",
  }));
  const dueAll = sims.filter(s => s.next_review <= today).sort(cmpDue);
  // 模拟今天排到的任务完成
  for (const s of dueAll.slice(0, DAILY_CAP)) {
    const ns = s.stage + 1;
    if (ns >= total) removeFrom(sims, s);
    else { s.stage = ns; s.next_review = addDaysISO(today, INTERVAL[ns]); }
  }
  const pending = dueAll.slice(DAILY_CAP);     // 今天没排上的，明天最优先
  const out = [];
  for (let off = 1; off <= days; off++) {
    const d = addDaysISO(today, off);
    for (const s of sims) if (s.next_review === d && pending.indexOf(s) < 0) pending.push(s);
    pending.sort(cmpDue);
    const take = pending.splice(0, DAILY_CAP);
    out.push({ date: d, items: take.map(t => ({ key: t.key, name: t.name, level: t.level, round: t.stage + 1 })) });
    for (const s of take) {
      const ns = s.stage + 1;
      if (ns >= total) removeFrom(sims, s);
      else { s.stage = ns; s.next_review = addDaysISO(d, INTERVAL[ns]); }
    }
  }
  return out;
}
function cmpDue(a, b) {
  if (a.next_review !== b.next_review) return a.next_review < b.next_review ? -1 : 1;
  return a.key < b.key ? -1 : a.key > b.key ? 1 : 0;
}

/* ---------------- 渲染：顶栏 & 今日任务 ---------------- */
function taskCard(p, opts) {
  const q = opts.queued;
  const key = esc(p.key);
  const doneT = opts.doneToday;
  const round = opts.round;                 // 轮次/顺延天数来自排期计算 opts，不在题目行里
  const late = opts.late;
  const chips =
    '<span class="round-chip">第 ' + round + " 轮复习</span>" +
    (late ? '<span class="late-chip">顺延 ' + late + " 天</span>" : "");
  const btnTxt = opts.doneToday
    ? "今天已完成"
    : (opts.queued ? "现在就做（第 " + round + " 轮）" : "完成第 " + round + " 轮复习");
  return (
    '<div class="task' + (q ? " task-q" : "") + '" id="due-' + key + '">' +
      '<div class="row1"><span class="name">' + esc(titleOf(p)) + "</span>" +
        levelSpan(p) + "</div>" +
      '<div class="row1"><div class="meta">' + chips + "</div></div>" +
      '<button class="btn btn-main" data-act="complete" data-key="' + key + '"' +
        (opts.doneToday ? " disabled" : "") + ">" + btnTxt + "</button>" +
      '<div class="row1"><div class="meta">' +
        '<button class="btn-mini" data-act="open" data-key="' + key + '">打开原题</button>' +
        '<button class="btn-mini" data-act="search" data-key="' + key + '">力扣搜索</button>' +
      "</div></div>" +
    "</div>"
  );
}

function renderHeaderChips() {
  const d = DATA;
  $("#todayLabel").textContent = fmtDate(d.today) + " · 每天最多 " + DAILY_CAP + " 道 · 每 " +
    INTERVAL.length + " 轮一循环";
  const chips = [
    ["今日完成", d.done_today.length],
    ["今日待做", d.todo.length + (d.queue.length ? "+" + d.queue.length : "")],
    ["累计做题", d.allHist],
    ["已通关", d.stats.done],
  ];
  $("#stats").innerHTML = chips.map(c =>
    '<div class="chip"><b>' + c[1] + "</b><span>" + c[0] + "</span></div>").join("");
}

function renderTodo() {
  const d = DATA;
  $("#todoHint").textContent = d.todo.length
    ? "按到期顺序自动排，最多 " + DAILY_CAP + " 道"
    : "";
  const box = $("#todoList");
  if (!d.todo.length) {
    box.innerHTML = '<div class="empty">今天没有要到期的题 —— 可以休息，或做一道新题登记进来，也可以到「题目库」提前复习。</div>';
    return;
  }
  box.innerHTML = d.todo.map(t => taskCard(t.p, { round: t.round, late: t.late })).join("");
}

function renderQueue() {
  const d = DATA;
  const sec = $("#queueSection");
  if (!d.queue.length) { sec.hidden = true; return; }
  sec.hidden = false;
  $("#queueHint").textContent = "还有 " + d.queue.length + " 道到期题排不上，会每天自动顶上，不会漏。想提前做可直接点下面的「现在就做」。";
  $("#queueList").innerHTML = d.queue.map(t => taskCard(t.p, { round: t.round, late: t.late, queued: true })).join("");
}

function renderDone() {
  const d = DATA;
  const box = $("#doneList");
  $("#doneHint").textContent = d.done_today.length ? "点「撤销」可退回完成前状态" : "";
  if (!d.done_today.length) {
    box.innerHTML = '<div class="empty">今天还没登记任何完成记录。</div>';
    return;
  }
  box.innerHTML = d.done_today.map(p => {
    const doneTxt = p.stage === 0 ? "今日新加入" : "已完成第 " + p.stage + " 轮";
    const key = esc(p.key);
    return (
      '<div class="done-item"><div class="l">' +
        '<span class="tick">&#10003;</span>' +
        '<span class="name">' + esc(titleOf(p)) + "</span>" +
        levelSpan(p) +
        '<span class="done-chip">' + doneTxt + "</span></div>" +
        '<div class="act" style="display:flex;gap:6px;align-items:center;flex:none">' +
          '<button class="btn-mini" data-act="open" data-key="' + key + '">原题</button>' +
          '<button class="btn btn-ghost" data-act="undo" data-key="' + key + '">撤销</button>' +
        "</div>" +
      "</div>"
    );
  }).join("");
}

function renderFuture() {
  const box = $("#futureList");
  const lines = DATA.future;
  if (!lines.some(f => f.items.length)) {
    box.innerHTML = '<div class="empty">未来 ' + PREVIEW_DAYS + " 天暂时没有排期。</div>";
    return;
  }
  box.innerHTML = lines.filter(f => f.items.length).map(f =>
    '<div class="future-line"><span class="d">' + shortDate(f.date) + " " + fmtDow(f.date) + "</span>" +
    '<span class="n">' + f.items.map(i =>
      esc(titleOf(i)) + "（第" + i.round + "轮）").join("、") + "</span></div>"
  ).join("");
}

/* ---------------- 渲染：日历 ---------------- */
function histMap() {
  const m = {};
  for (const p of PROBLEMS) {
    for (const d of (p.history || [])) m[d] = (m[d] || 0) + 1;
  }
  return m;
}
function planCountFor(iso) {
  if (iso === todayISO()) return DATA.todo.length;
  if (iso < todayISO()) return 0;
  return PROBLEMS.filter(p => !p.completed && p.next_review === iso).length;
}

function renderCal() {
  if (!DATA) return;
  if (!CAL) {
    const n = new Date();
    CAL = { y: n.getFullYear(), m: n.getMonth() };
  }
  const { y, m } = CAL;
  const today = todayISO();
  $("#calTitle").textContent = y + " 年 " + (m + 1) + " 月";
  const doneMap = histMap();
  const firstDow = new Date(y, m, 1).getDay();
  const dim = new Date(y, m + 1, 0).getDate();

  let h = WEEK_SHORT.map(w => '<div class="cal-dow">周' + w + "</div>").join("");
  for (let i = 0; i < 42; i++) {
    const d = new Date(y, m, 1 - firstDow + i);
    const iso = isoOf(d);
    const inMonth = d.getMonth() === m;
    const doneN = doneMap[iso] || 0;
    const planN = iso >= today ? planCountFor(iso) : 0;
    const queueN = iso === today ? DATA.queue.length : 0;
    let marks = "";
    if (doneN) marks += '<span class="mk mk-do">做 ' + doneN + "</span>";
    if (planN) marks += '<span class="mk mk-plan">' + (iso === today ? "任务 " : "排 ") + planN + "</span>";
    if (queueN) marks += '<span class="mk mk-queue">排队 ' + queueN + "</span>";
    const cls = "cal-cell" + (inMonth ? "" : " other") + (iso === today ? " today" : "");
    h += '<div class="' + cls + '" data-day="' + iso + '">' +
         '<div class="cn">' + d.getDate() + "</div>" +
         (marks ? '<div class="marks">' + marks + "</div>" : "") +
         "</div>";
  }
  $("#calGrid").innerHTML = h;
  $("#calHint").textContent = "当月累计完成 " + (Object.keys(doneMap)
    .filter(k => k.slice(0, 7) === y + "-" + pad2(m + 1))
    .reduce((s, k) => s + doneMap[k], 0)) + " 次 · 点击日期查看详情";
}

/* ---------------- 渲染：统计 ---------------- */
function periodRange(p) {
  const n = new Date();
  const t = todayISO();
  const y = n.getFullYear();
  let start, end;
  if (p === "week") {
    start = addDaysISO(t, -((n.getDay() + 6) % 7));   // 周一
    end = addDaysISO(start, 6);
  } else if (p === "month") {
    start = y + "-" + pad2(n.getMonth() + 1) + "-01";
    end = isoOf(new Date(y, n.getMonth() + 1, 0));
  } else if (p === "year") {
    start = y + "-01-01"; end = y + "-12-31";
  } else {
    start = "0000-00-00"; end = "9999-99-99";
  }
  return { start, end, label: shortDate(start) + " ~ " + shortDate(end) };
}
function statForRange(start, end) {
  let cnt = 0, added = 0, fin = 0;
  const keys = new Set(), byDate = {};
  for (const p of PROBLEMS) {
    for (const d of (p.history || [])) {
      if (d >= start && d <= end) {
        cnt++;
        keys.add(p.key);
        (byDate[d] = byDate[d] || []).push(p);
      }
    }
    const ad = p.added || "";
    if (ad && ad >= start && ad <= end) added++;
    const cd = p.completed_date || "";
    if (cd && cd >= start && cd <= end) fin++;
  }
  return { cnt, uniq: keys.size, added, fin, byDate };
}

function renderStats() {
  if (!DATA) return;
  const r = periodRange(STAT_PERIOD);
  const s = statForRange(r.start, r.end);
  const cards = [
    [s.cnt, "做题次数"],
    [s.uniq, "不同题目"],
    [s.added, "新学新题"],
    [s.fin, "通关数"],
  ];
  $("#statCards").innerHTML = cards.map(c =>
    '<div class="stat-card"><b>' + c[0] + "</b><span>" + c[1] + "</span></div>").join("");
  const lbl = STAT_PERIOD === "all" ? "累计"
    : STAT_PERIOD === "week" ? "本周" : STAT_PERIOD === "month" ? "本月" : "今年";
  $("#statDetailTitle").textContent = lbl + "做题明细（" + r.label + "） · 共 " + s.cnt + " 次";
  const dates = Object.keys(s.byDate).sort().reverse();
  const box = $("#statDetail");
  if (!dates.length) {
    box.innerHTML = '<div class="stat-day"><span class="d">—</span><span class="n">该时间段还没有做题记录。</span></div>';
    return;
  }
  box.innerHTML = dates.map(d => {
    const ps = s.byDate[d];
    const names = ps.slice(0, 8).map(p => esc(titleOf(p))).join("、");
    return '<div class="stat-day"><span class="d">' + shortDate(d) + " " + fmtDow(d) + "</span>" +
      '<span class="n">' + names + (ps.length > 8 ? " …等 " + ps.length + " 题" : "") + "</span>" +
      '<span class="c">' + ps.length + " 次</span></div>";
  }).join("");
}

/* ---------------- 渲染：题目库 ---------------- */
function libFilter() {
  const q = libQ.toLowerCase();
  const dueSet = new Set();
  DATA.todo.forEach(t => dueSet.add(t.p.key));
  const queueSet = new Set(DATA.queueKeys);
  const today = todayISO();
  const list = [];
  for (const p of PROBLEMS) {
    if (libLv && p.level !== libLv) continue;
    if (libSt === "done" && !p.completed) continue;
    if (libSt === "active" && p.completed) continue;
    if (libSt === "due" && !dueSet.has(p.key)) continue;
    if (libSt === "queue" && !queueSet.has(p.key)) continue;
    if (q) {
      const hay = (String(p.key) + " " + String(p.id != null ? p.id : "") + " " + (p.name || "")).toLowerCase();
      if (hay.indexOf(q) < 0) continue;
    }
    list.push(p);
  }
  list.sort((a, b) => {
    if (!!a.completed !== !!b.completed) return a.completed ? 1 : -1;
    const ai = a.id, bi = b.id;
    if (ai != null && bi != null && ai !== bi) return (ai < bi ? -1 : 1);
    return (a.key < b.key ? -1 : 1);
  });
  return { list, dueSet, queueSet };
}

function libStatusInfo(p) {
  const today = todayISO();
  if (p.completed) return { txt: "已通关 " + (p.completed_date || ""), hot: false };
  if (p.last_done === today) return { txt: "今天已做 · 第 " + p.stage + " 轮", hot: false };
  if ((p.next_review || "9999") <= today) {
    return { txt: "已到期 · 顺延 " + daysLate(p.next_review, today) + " 天", hot: true };
  }
  return { txt: "进行中 · 下次 " + shortDate(p.next_review), hot: false };
}

function renderLib() {
  if (!DATA) return;
  const f = libFilter();
  $("#libHint").textContent = "共 " + f.list.length + " 道 / 全部 " + PROBLEMS.length +
    " 道（进行中 " + DATA.stats.active + " · 已通关 " + DATA.stats.done + "）";
  const box = $("#libList");
  if (!f.list.length) {
    box.innerHTML = '<div class="empty">没有符合条件的题目。</div>';
    return;
  }
  const today = todayISO();
  const total = INTERVAL.length;
  box.innerHTML = f.list.map(p => {
    const st = libStatusInfo(p);
    const key = esc(p.key);
    const doneT = p.last_done === today && !p.completed;
    let dots = "";
    const on = p.completed ? total : p.stage;
    for (let i = 0; i < total; i++) {
      const cls = i < on ? (p.completed ? "d2 on fin" : "d2 on") : "d2";
      dots += '<i class="' + cls + '"></i>';
    }
    const act = [];
    if (!p.completed) {
      if (doneT) act.push('<button class="btn-mini danger" data-act="undo" data-key="' + key + '">撤销今天</button>');
      else act.push('<button class="btn-mini ok" data-act="complete" data-key="' + key + '">' +
        ((p.next_review || "9999") <= today ? "完成本轮" : "提前复习") + "</button>");
    }
    act.push('<button class="btn-mini" data-act="open" data-key="' + key + '">原题</button>');
    act.push('<button class="btn-mini" data-act="search" data-key="' + key + '">搜索</button>');
    return (
      '<div class="lib-row">' +
        '<div class="grow">' +
          '<div class="t1"><span class="name">' + esc(titleOf(p)) + "</span>" + levelSpan(p) + "</div>" +
          '<div class="meta">' +
            '<span class="' + (st.hot ? "late-chip" : "done-chip") + '">' + st.txt + "</span>" +
            (p.completed ? "" : '<span>已完成 ' + p.stage + " / " + total + " 轮</span>") +
            '<span class="dots-sm">' + dots + "</span>" +
          "</div>" +
        "</div>" +
        '<div class="act">' + act.join("") + "</div>" +
      "</div>"
    );
  }).join("");
}

/* ---------------- 渲染汇总 ---------------- */
function renderAll() {
  if (!DATA) return;
  renderHeaderChips();
  renderTodo();
  renderQueue();
  renderDone();
  renderFuture();
  renderCal();
  renderStats();
  renderLib();
  if (dayOpen) openDay(dayOpen);   // 日历详情弹窗若开着，刷新其内容
}

/* ---------------- 数据加载 ---------------- */
async function loadData(silent) {
  if (!SB) return;
  try {
    await ensureMeta();
    const { data, error } = await SB.from("problems").select("*").order("key");
    if (error) throw error;
    PROBLEMS = data || [];
    DATA = buildData();
    renderAll();
    $("#footError").textContent = "";
  } catch (e) {
    console.error(e);
    $("#footError").textContent = "读取云端数据失败：" + (e.message || e);
    if (!silent) toast("读取云端数据失败，请稍后再试", true);
  }
}

/* ---------------- 完成 / 撤销 / 新题 ---------------- */
async function completeKey(key, btn) {
  if (BUSY) return null;
  BUSY = true;
  const date = todayISO();
  let msg = null;
  let btnOrig = "";
  if (btn) {
    btnOrig = btn.textContent;
    btn.disabled = true;
    btn.textContent = "处理中…";
  }
  try {
    const p = PROBLEMS.find(x => x.key === key);
    if (!p) msg = { ok: false, error: "库中找不到该题" };
    else if (p.completed)
      msg = { ok: false, error: "「" + p.name + "」已完成全部 " + INTERVAL.length + " 轮，无需再登记" };
    else if (p.last_done === date)
      msg = { ok: false, error: "「" + p.name + "」今天已经登记过了" };
    else {
      const snapshot = { stage: p.stage, last_done: p.last_done, next_review: p.next_review, completed: p.completed };
      const ns = p.stage + 1;
      let completed = false, next = null, completed_date = null;
      if (ns >= INTERVAL.length) { completed = true; completed_date = date; }
      else next = addDaysISO(date, INTERVAL[ns]);
      const history = (p.history || []).concat([date]);
      const upd = {
        stage: ns, last_done: date, next_review: next,
        completed: completed, completed_date: completed_date,
        snapshot: snapshot, history: history,
      };
      const { error } = await SB.from("problems").update(upd).eq("key", key);
      if (error) throw error;
      msg = completed
        ? { ok: true, message: "通关「" + p.name + "」——全部 " + INTERVAL.length + " 轮复习完成！" }
        : { ok: true, message: "完成「" + p.name + "」第 " + ns + " 轮 · 下次复习 " + next };
    }
  } catch (e) {
    msg = { ok: false, error: "操作失败：" + (e.message || e) };
  }
  if (btn) {
    btn.disabled = false;
    btn.textContent = btnOrig;
  }
  BUSY = false;
  toast(msg.message || msg.error, !msg.ok);
  if (msg.ok) await loadData(true);
  return msg;
}

async function undoKey(key) {
  if (BUSY) return;
  BUSY = true;
  try {
    const date = todayISO();
    const p = PROBLEMS.find(x => x.key === key);
    if (!p) { toast("库中找不到该题", true); return; }
    const snap = p.snapshot;
    if (!snap) { toast("没有可撤销的记录", true); return; }
    if (p.last_done !== date) { toast("只能撤销今天通过网页完成的登记", true); return; }
    const history = (p.history || []).slice();
    if (history.length && history[history.length - 1] === date) history.pop();
    const upd = {
      stage: snap.stage,
      last_done: snap.last_done || null,
      completed: !!snap.completed,
      next_review: snap.next_review || null,
      completed_date: null,
      snapshot: null,
      history: history,
    };
    const { error } = await SB.from("problems").update(upd).eq("key", key);
    if (error) throw error;
    toast("已撤销「" + p.name + "」今天的登记");
    await loadData(true);
  } catch (e) {
    toast("操作失败：" + (e.message || e), true);
  } finally {
    BUSY = false;
  }
}

function findProblem(token) {
  const t = token.trim();
  const exact = PROBLEMS.find(p => p.key === t);
  if (exact) return exact;
  if (/^\d+$/.test(t)) return null;                 // 数字但库里没有 → 需要新题弹窗
  const low = t.toLowerCase();
  const hits = PROBLEMS.filter(p => String(p.name || "").toLowerCase().includes(low));
  if (hits.length === 1) return hits[0];
  if (hits.length > 1) toast("「" + t + "」匹配到多道题，请写更具体", true);
  return null;
}

function openQuickAdd(value) {
  const numeric = /^\d+$/.test(value);
  $("#newKey").value = numeric ? value : "";
  $("#newName").value = numeric ? "" : value;
  $("#newName").focus();
  $("#modal").classList.add("show");
}

function closeQuickAdd() {
  $("#modal").classList.remove("show");
}

async function submitQuickAdd() {
  const key = $("#newKey").value.trim();
  const name = $("#newName").value.trim();
  const level = $("#newLevel").value;
  if (!name) { toast("题目名不能为空", true); return; }
  const finalKey = key || name;
  if (PROBLEMS.some(p => p.key === finalKey)) { toast("题号「" + finalKey + "」已在系统中", true); return; }
  if (PROBLEMS.some(p => String(p.name || "").toLowerCase() === name.toLowerCase())) {
    toast("「" + name + "」已在系统中，直接用它的题号登记即可", true);
    return;
  }
  const date = todayISO();
  const row = {
    key: finalKey,
    id: /^\d+$/.test(finalKey) ? parseInt(finalKey, 10) : null,
    name: name, level: level, added: date,
    stage: 0, completed: false, last_done: date,
    next_review: addDaysISO(date, INTERVAL[0]),
    history: [date], archived: false,
  };
  try {
    const { error } = await SB.from("problems").insert(row);
    if (error) throw error;
    closeQuickAdd();
    toast("新题「" + name + "」已加入复习计划 · 明天进入第一轮");
    $("#regInput").value = "";          // 清空快速登记输入框
    await loadData(true);
  } catch (e) {
    if (e.code === "23505") toast("该题已存在", true);
    else toast("加入失败：" + (e.message || e), true);
  }
}

/* ---------------- 日期详情弹窗 ---------------- */
function openDay(iso) {
  if (!DATA) return;
  dayOpen = iso;
  const today = todayISO();
  $("#dayModal").classList.add("show");
  $("#dayTitle").textContent = fmtDate(iso);
  const box = $("#dayBody");
  let h = "";

  // 今天/未来的安排
  if (iso >= today) {
    let planned = [], queuedN = 0;
    if (iso === today) {
      DATA.todo.forEach(t => planned.push({ p: t.p, round: t.round, late: t.late }));
      queuedN = DATA.queue.length;
    } else {
      planned = PROBLEMS.filter(p => !p.completed && p.next_review === iso)
        .map(p => ({ p, round: p.stage + 1 }));
    }
    const undone = planned.filter(x => x.p.last_done !== today);
    if (undone.length || queuedN) {
      h += '<h4>' + (iso === today ? "今天的任务" : "当天排期") + "（" +
        undone.length + (queuedN ? " + 排队" + queuedN : "") + "）</h4>";
      undone.forEach(x => {
        const p = x.p, key = esc(p.key);
        h += '<div class="day-row"><span class="name">' + esc(titleOf(p)) + "</span>" +
          levelSpan(p) +
          '<span class="round-chip">第 ' + x.round + " 轮</span>" +
          (x.late ? '<span class="late-chip">顺延 ' + x.late + " 天</span>" : "") +
          '<button class="btn-mini ok" data-act="complete" data-key="' + key + '">现在做</button>' +
          '<button class="btn-mini" data-act="open" data-key="' + key + '">原题</button></div>';
      });
      if (queuedN) h += '<div class="day-empty">另有 ' + queuedN +
        " 道到期题在排队，做完上面的会自动顶上（可去「今日任务 / 题目库」处理）。</div>";
    }
  }

  // 完成记录
  const donePs = PROBLEMS.filter(p => (p.history || []).indexOf(iso) >= 0);
  h += "<h4>当天完成记录（" + donePs.length + "）</h4>";
  if (!donePs.length) {
    h += '<div class="day-empty">这天没有完成记录。' + (iso === today
      ? "做完上面的题就会出现在这里。" : "") + "</div>";
  } else {
    donePs.sort((a, b) => (a.key < b.key ? -1 : 1));
    h += donePs.map(p => {
      const key = esc(p.key);
      const canUndo = iso === today && p.last_done === today && !p.completed;
      return '<div class="day-row"><span class="name">' + esc(titleOf(p)) + "</span>" +
        levelSpan(p) +
        '<span class="cnt">' + (p.completed ? "已通关" : "第 " + p.stage + " 轮") + "</span>" +
        '<button class="btn-mini" data-act="open" data-key="' + key + '">原题</button>' +
        (canUndo ? '<button class="btn-mini danger" data-act="undo" data-key="' + key + '">撤销</button>' : "") +
        "</div>";
    }).join("");
  }
  box.innerHTML = h;
}

/* ---------------- 撤销两段确认 ---------------- */
let undoArm = null;
function undoDisarm() {
  if (!undoArm) return;
  clearTimeout(undoArm.timer);
  undoArm.btn.textContent = undoArm.orig || "撤销";
  undoArm.btn.classList.remove("armed");
  undoArm = null;
}
function onUndoClick(btn) {
  if (BUSY) return;
  if (undoArm && undoArm.btn === btn) {
    clearTimeout(undoArm.timer);
    undoArm = null;
    btn.classList.remove("armed");
    undoKey(btn.dataset.key);
    return;
  }
  undoDisarm();
  undoArm = {
    btn: btn, timer: setTimeout(undoDisarm, 3000),
    orig: btn.textContent || "撤销",
  };
  btn.textContent = "再点一次确认撤销";
  btn.classList.add("armed");
}

/* ---------------- 标签页 / 日期导航 ---------------- */
function switchTab(name) {
  document.querySelectorAll(".tab").forEach(t =>
    t.classList.toggle("active", t.dataset.tab === name));
  document.querySelectorAll(".pane").forEach(p =>
    p.classList.toggle("active", p.id === "tab-" + name));
}
function calNav(delta) {
  CAL.m += delta;
  if (CAL.m < 0) { CAL.m = 11; CAL.y--; }
  if (CAL.m > 11) { CAL.m = 0; CAL.y++; }
  renderCal();
}
function calGotoToday() {
  const n = new Date();
  CAL = { y: n.getFullYear(), m: n.getMonth() };
  renderCal();
}

/* ---------------- 访问口令 ---------------- */
const authView = $("#loginView");
const appView = $("#appView");
const LS_KEY = "lc_pass_ok";     // 同一浏览器输过一次口令后记住，下次直接进

async function enterApp() {
  try {
    await ensureClient();
  } catch (e) {
    $("#loginErr").textContent = e.message;
    return;
  }
  authView.hidden = true;
  appView.hidden = false;
  try { localStorage.setItem(LS_KEY, ACCESS_CODE); } catch (e) { /* 隐私模式忽略 */ }
  if (!DATA) loadData(false);
}

/* ---------------- 事件绑定 ---------------- */
document.addEventListener("click", (e) => {
  const dayCell = e.target.closest("[data-day]");
  if (dayCell) { openDay(dayCell.dataset.day); return; }

  const pbtn = e.target.closest("[data-period]");
  if (pbtn) {
    STAT_PERIOD = pbtn.dataset.period;
    document.querySelectorAll("#periodBar .pbtn").forEach(b =>
      b.classList.toggle("active", b === pbtn));
    renderStats();
    return;
  }

  const tabBtn = e.target.closest("[data-tab]");
  if (tabBtn) { switchTab(tabBtn.dataset.tab); return; }

  const b = e.target.closest("[data-act]");
  if (!b) return;
  const act = b.dataset.act, key = b.dataset.key;
  if (act === "complete") completeKey(key, b);
  else if (act === "undo") onUndoClick(b);
  else if (act === "open") { const p = probByKey(key); if (p) doOpen(p, b); }
  else if (act === "search") { const p = probByKey(key); if (p) window.open(searchUrlOf(p), "_blank"); }
  else if (act === "cal-prev") calNav(-1);
  else if (act === "cal-next") calNav(1);
  else if (act === "cal-today") calGotoToday();
  else if (act === "day-close") { dayOpen = null; $("#dayModal").classList.remove("show"); }
});

$("#dayModal").addEventListener("click", (e) => {
  if (e.target.id === "dayModal") { dayOpen = null; $("#dayModal").classList.remove("show"); }
});

$("#codeForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const code = $("#accessCode").value.trim();
  if (code !== ACCESS_CODE) {
    $("#loginErr").textContent = "口令不对，请重试";
    return;
  }
  await enterApp();
});

$("#regForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const input = $("#regInput");
  const v = input.value.trim();
  if (!v) return;
  const hit = findProblem(v);
  if (hit) {
    if (hit.completed) { toast("「" + hit.name + "」已通关，不用再做", true); input.value = ""; return; }
    const r = await completeKey(hit.key, $("#regForm button"));
    if (r && r.ok) input.value = "";
  } else {
    openQuickAdd(v);
  }
});

$("#addForm").addEventListener("submit", (e) => {
  e.preventDefault();
  submitQuickAdd();
});
$("#addCancel").addEventListener("click", closeQuickAdd);

$("#libSearch").addEventListener("input", (e) => { libQ = e.target.value.trim(); renderLib(); });
$("#libLevel").addEventListener("change", (e) => { libLv = e.target.value; renderLib(); });
$("#libStatus").addEventListener("change", (e) => { libSt = e.target.value; renderLib(); });

/* ---------------- 启动 ---------------- */
(async function boot() {
  loadSlugCache();
  let saved = null;
  try { saved = localStorage.getItem(LS_KEY); } catch (e) { /* 忽略 */ }
  if (saved === ACCESS_CODE) {
    try {
      await ensureClient();
      await enterApp();
      return;
    } catch (e) {
      $("#loginErr").textContent = e.message;
      return;
    }
  }
  if (!CFG.supabaseUrl || CFG.supabaseUrl.includes("YOUR-") ||
      !CFG.supabaseAnonKey || CFG.supabaseAnonKey.includes("YOUR-")) {
    $("#loginErr").textContent = "config.js 尚未填入 Supabase 地址与 anon key（配置好后刷新页面）";
  }
})();
