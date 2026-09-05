# LeetCode 艾宾浩斯复习 · 云端版

把本地复习系统搬到云端：**Vercel 托管网页 + Supabase 存数据**。
电脑不用开机、任何设备打开网址输入访问口令即可使用；排期算法与原来完全一致。

```
仓库根（cwzjason/leetcode）
├── vercel.json          部署配置：静态输出目录指向 review
└── review/              网页代码
    ├── index.html           页面（访问口令 + 复习面板）
    ├── style.css            样式
    ├── app.js               逻辑（读 Supabase、排期计算、完成/撤销/新题）
    ├── config.js            ★ 配置中心：Supabase 地址 / anon key / 访问口令
    └── supabase/
        ├── schema.sql           全新项目：直接执行建表（无登录版）
        └── migration-anon.sql   已用旧版建过表：执行这段完成去登录迁移
```

> 说明 1：Supabase 的客户端库不放进仓库。浏览器打开网页时，
> 会自动从 CDN（unpkg）加载它再连接数据库。如果某天 CDN 访问异常，
> 把 `app.js` 顶部的 `SUPABASE_CDN` 换成
> `https://cdn.jsdelivr.net/npm/@supabase/supabase-js@2/dist/umd/supabase.js`。
>
> 说明 2：本工具是个人单用户，不需要邮箱账号。打开网页时输入
> `config.js` 里的访问口令（默认 `cwz`）即可进入复习面板。

## 使用流程（按顺序）

### 1. Vercel 部署
1. 打开 [vercel.com](https://vercel.com) → 用 GitHub 登录 → **Add New Project**；
2. Import 仓库 `cwzjason/leetcode`；
3. Framework Preset 选 **Other**；Root Directory 保持默认（仓库根 `vercel.json` 已把输出目录指向 `review`）；
4. 直接 **Deploy**。部署成功后得到一个网址，如 `https://leetcode-xxx.vercel.app`。

### 2. Supabase 建库
1. 打开 [supabase.com](https://supabase.com) 注册 → **New Project**（地区可选 Singapore 附近）；
2. 项目建好后：左侧 **SQL Editor** → **New query**：
   - **全新项目**：粘贴 `review/supabase/schema.sql` 全部内容 → **Run**；
   - **之前已用旧脚本建过表**：改粘贴 `review/supabase/migration-anon.sql` → **Run**；
3. 验证：SQL Editor 里执行 `select * from public.meta;`，能查到一行即成功。

### 3. 填配置并重新部署
1. 回到 Supabase：**项目设置 Settings → API**，复制 **Project URL** 和 **anon public** key；
2. 打开本目录的 `config.js`：
   - 替换 `supabaseUrl` / `supabaseAnonKey` 两个占位符；
   - `accessCode` 是打开网页要输的口令（默认 `cwz`），想换就顺手改；
3. `git add -A && git commit && git push` → Vercel 检测到推送自动重新部署（等 1 分钟）；
4. 打开 Vercel 网址 → 输入访问口令 → 开始使用（同一浏览器只需输一次，之后自动记住）。

### 4. 日常使用
- 到期复习的题：点「完成第 X 轮复习」→ 自动排下一次；
- 点错可点「撤销」（再点一次确认）；
- 做了一道系统里没有的新题：顶部输入框填**题号**（如 `977`）或**题名**回车
  → 弹出小窗补全题目名/难度 → 加入排期（今天算新学，明天开始复习）；
- 未完成的题自动顺延，通关 7 轮后自动进入「已通关」。

## 数据安全（坦诚说明）
- 本版本为方便使用，去掉了邮箱登录，改用前端访问口令（`config.js` 的 `accessCode`）。
- 前端口令**只能挡住随手打开网址的人**；懂技术的人查看网页源码
  就能看到口令明文——纯静态网页无法做到更强，除非回到邮箱登录方案。
- Supabase 的 anon key 本来就是公开给浏览器的（设计如此）。数据库 RLS
  已关闭，拿到 anon key 即可读写。对"个人使用 + 不公开分享网址"足够；
  若以后在意安全性，可回退到邮箱登录方案（见 git 历史）。

## 常见问题
- **打开页面提示 config.js 未配置**：还没填 Supabase 地址/anon key，按步骤 3 操作。
- **口令忘了**：打开 `config.js` 看 `accessCode`（也可以改成自己好记的）。
- **页面空白/读不到数据**：多半是 `config.js` 还是占位符，或 SQL 没执行成功。
- **Supabase 免费项目闲置 7 天会暂停**：只要坚持每天用（页面会请求数据库），就不会停；
  真停了去 Dashboard 点一下恢复即可。
