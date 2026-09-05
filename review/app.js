"use strict";

/* =================================================================
 * LeetCode 艾宾浩斯复习 · 云端版逻辑
 * 数据存 Supabase；排期算法与本地版完全一致：
 *   点「完成」→ 轮次 +1 → 按 interval 计算下次复习日期；
 *   攒满 7 轮 → 通关归档。页面每次打开实时计算「今天要做」。
 * 访问方式：打开网页输入访问口令（config.js 的 accessCode）即可进入，
 *           不需要邮箱账号。
 * ================================================================= */

const CFG = window.APP_CONFIG || {};
const ACCESS_CODE = (CFG.accessCode || "cwz");   // 打开网页需输入的访问口令（config.js 可改）
let SB = null;
const SUPABASE_CDN = "https://unpkg.com/@supabase/supabase-js@2/dist/umd/supabase.js";

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

// 客户端库不需要提前下载/打包：打开网页时浏览器自动从 CDN 加载，
// 加载成功后才能连接 Supabase（进入页面之前完成）。
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
function pad2(n) { return (n < 10 ? "0" : "") + n; }
function todayISO() {
  const d = new Date();
  return d.getFullYear() + "-" + pad2(d.getMonth() + 1) + "-" + pad2(d.getDate());
}
function addDaysISO(iso, n) {
  const p = iso.split("-").map(Number);
  const d = new Date(p[0], p[1] - 1, p[2] + n);
  return d.getFullYear() + "-" + pad2(d.getMonth() + 1) + "-" + pad2(d.getDate());
}
function fmtDate(iso) {
  const d = new Date(iso + "T00:00:00");
  return d.getFullYear() + " 年 " + (d.getMonth() + 1) + " 月 " + d.getDate() + " 日 · " + WEEK[d.getDay()];
}
function shortDate(iso) {
  const p = iso.split("-");
  return (p[1] | 0) + "月" + (p[2] | 0) + "日";
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
  const id = (p && p.id) ? String(p.id) : "";
  return (id ? id + ". " : "") + (p && p.name ? p.name : "");
}

/* ---------------- 数据与状态 ---------------- */
let PROBLEMS = [];                       // 云端 problems 表全部行
let INTERVAL = Array.isArray(CFG.interval) ? CFG.interval : [1, 2, 4, 7, 15, 30, 60];
let DATA = null;                         // buildData() 的渲染快照
let BUSY = false;

async function ensureMeta() {
  const { data } = await SB.from("meta").select("interval").eq("id", 1).maybeSingle();
  if (data && data.interval) { INTERVAL = data.interval; return; }
  const { error } = await SB.from("meta").insert({ id: 1, interval: INTERVAL });
  if (error && error.code !== "23505") throw error;
  const again = await SB.from("meta").select("interval").eq("id", 1).maybeSingle();
  if (again.data && again.data.interval) INTERVAL = again.data.interval;
}

function buildData() {
  const date = todayISO();
  const due = [], doneToday = [], done = [];
  const active = PROBLEMS.filter(p => !p.completed);
  for (const p of PROBLEMS) {
    const row = {
      key: p.key, id: p.id, name: p.name, level: p.level || "?",
      stage: p.stage, total: INTERVAL.length,
      completed: p.completed, next_review: p.next_review,
      last_done: p.last_done, completed_date: p.completed_date,
    };
    if (p.completed) { done.push(row); continue; }
    if (p.last_done === date) { row.done_today = true; doneToday.push(row); continue; }
    if ((p.next_review || "9999") <= date) {
      row.round = p.stage + 1;
      row.late = daysLate(p.next_review, date);
      due.push(row);
    }
  }
  due.sort((a, b) => (a.next_review < b.next_review ? -1 : 1));
  doneToday.sort((a, b) => (a.key < b.key ? -1 : 1));

  const future = [];
  for (let off = 1; off <= 3; off++) {
    const d = addDaysISO(date, off);
    const items = [];
    for (const p of PROBLEMS) {
      if (!p.completed && p.next_review === d) items.push({ id: p.id, name: p.name, round: p.stage + 1 });
    }
    future.push({ date: d, items: items });
  }

  done.sort((a, b) => {
    const x = a.completed_date || "", y = b.completed_date || "";
    return x === y ? (a.key < b.key ? -1 : 1) : (x < y ? -1 : 1);
  });

  return {
    ok: true, today: date, interval: INTERVAL,
    due: due, done_today: doneToday, future: future,
    problems: active.concat(done),
    stats: { active: active.length, done: done.length, total: PROBLEMS.length },
  };
}

/* ---------------- 渲染 ---------------- */
function renderStats() {
  const s = DATA.stats;
  $("#stats").innerHTML =
    '<div class="chip"><b>' + s.active + "</b><span>进行中</span></div>" +
    '<div class="chip"><b>' + s.done + "</b><span>已通关</span></div>" +
    '<div class="chip"><b>' + s.total + "</b><span>题库</span></div>";
}

function renderDue() {
  const due = DATA.due;
  $("#dueHint").textContent = due.length ? "到期 " + due.length + " 题 · 做完点「完成」即可" : "";
  const box = $("#dueList");
  if (!due.length) {
    box.innerHTML = '<div class="empty">今天没有到期要复习的题 —— 可以休息，或者做一道新题登记进来。</div>';
    return;
  }
  box.innerHTML = due.map(p => {
    const chips =
      '<span class="round-chip">第 ' + p.round + " 轮复习</span>" +
      (p.late ? '<span class="late-chip">已顺延 ' + p.late + " 天</span>" : "");
    return (
      '<div class="task" id="due-' + esc(p.key) + '">' +
        '<div class="row1"><span class="name">' + esc(titleOf(p)) + "</span>" +
          '<span class="level ' + levelClass(p.level) + '">' + esc(p.level) + "</span></div>" +
        '<div class="row1"><div class="meta">' + chips + "</div></div>" +
        '<button class="btn btn-main" data-act="complete" data-key="' + esc(p.key) + '">' +
          "完成第 " + p.round + " 轮复习</button>" +
      "</div>"
    );
  }).join("");
}

function renderDone() {
  const done = DATA.done_today;
  $("#doneHint").textContent = done.length ? "点「撤销」可退回完成前状态" : "";
  const box = $("#doneList");
  if (!done.length) {
    box.innerHTML = '<div class="empty">今天还没登记任何完成记录。</div>';
    return;
  }
  box.innerHTML = done.map(p => {
    const doneTxt = p.stage === 0 ? "今日新加入" : "已完成第 " + p.stage + " 轮";
    return (
      '<div class="done-item"><div class="l">' +
        '<span class="tick">&#10003;</span>' +
        '<span class="name">' + esc(titleOf(p)) + "</span>" +
        '<span class="done-chip">' + doneTxt + "</span></div>" +
        '<button class="btn btn-ghost" data-act="undo" data-key="' + esc(p.key) + '">撤销</button>' +
      "</div>"
    );
  }).join("");
}

function renderFuture() {
  const box = $("#futureList");
  const lines = DATA.future;
  if (!lines.some(f => f.items.length)) {
    box.innerHTML = '<div class="empty">未来 3 天暂时没有排期。</div>';
    return;
  }
  box.innerHTML = lines.map(f => {
    if (!f.items.length) return "";
    return (
      '<div class="future-line"><span class="d">' + shortDate(f.date) + "</span>" +
      '<span class="n">' + f.items.map(i => esc(titleOf(i)) + "（第" + i.round + "轮）").join("、") + "</span></div>"
    );
  }).join("");
}

function renderProgress() {
  const box = $("#progressList");
  const ps = DATA.problems;
  if (!ps.length) {
    box.innerHTML = '<div class="empty">题库是空的 —— 用下面的「登记 / 新题」加入第一道题吧。</div>';
    return;
  }
  box.innerHTML = ps.map(p => {
    let statusHtml, dotsHtml = "";
    const total = p.total || INTERVAL.length;
    let on = 0, isFin = false, nextIdx = -1;
    if (p.completed) {
      on = total;
      statusHtml = '<span class="status">已通关 · ' + esc(p.completed_date || "") + "</span>";
      isFin = true;
    } else {
      on = p.stage;
      const next = p.next_review;
      const due = next <= DATA.today;
      statusHtml = due
        ? '<span class="status hot">' + (next === DATA.today ? "今天到期" : "已到期") + "</span>"
        : '<span class="status">下次 ' + shortDate(next) + "</span>";
      if (on < total) nextIdx = on;
    }
    for (let i = 0; i < total; i++) {
      let cls = "dot";
      if (i < on) cls += isFin ? " on fin" : " on";
      else if (i === nextIdx) cls += " next";
      dotsHtml += '<span class="' + cls + '"></span>';
    }
    return (
      '<div class="prog-item">' +
        '<div class="row"><span class="name">' + esc(titleOf(p)) + "</span>" +
          '<span class="level ' + levelClass(p.level) + '">' + esc(p.level) + "</span>" +
          statusHtml + "</div>" +
        (p.completed ? "" : '<div class="meta" style="margin-top:2px;color:var(--muted);font-size:12px">' +
          "已完成 " + p.stage + " / " + total + " 轮" + "</div>") +
        '<div class="dots">' + dotsHtml + "</div>" +
      "</div>"
    );
  }).join("");
}

function renderAll() {
  const d = DATA;
  $("#todayLabel").textContent = fmtDate(d.today) + " · 每 " + d.interval.length + " 轮一循环";
  renderStats();
  renderDue();
  renderDone();
  renderFuture();
  renderProgress();
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
  const hits = PROBLEMS.filter(p => String(p.name).toLowerCase().includes(low));
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
  if (PROBLEMS.some(p => String(p.name).toLowerCase() === name.toLowerCase())) {
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
    toast("新题「" + name + "」已加入复习计划 · 下次复习 " + row.next_review);
    await loadData(true);
  } catch (e) {
    if (e.code === "23505") toast("该题已存在", true);
    else toast("加入失败：" + (e.message || e), true);
  }
}

/* ---------------- 撤销两段确认 ---------------- */
let undoArm = null;
function undoDisarm() {
  if (!undoArm) return;
  clearTimeout(undoArm.timer);
  undoArm.btn.textContent = "撤销";
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
  undoArm = { btn: btn, timer: setTimeout(undoDisarm, 3000) };
  btn.textContent = "再点一次确认撤销";
  btn.classList.add("armed");
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
  const b = e.target.closest("[data-act]");
  if (!b) return;
  const act = b.dataset.act, key = b.dataset.key;
  if (act === "complete") completeKey(key, b);
  if (act === "undo") onUndoClick(b);
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

/* ---------------- 启动 ---------------- */
(async function boot() {
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
