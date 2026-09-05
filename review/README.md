# LeetCode 艾宾浩斯复习 · 云端版

把本地复习系统搬到云端：**Vercel 托管网页 + Supabase 存数据**。
电脑不用开机、任何设备登录即可访问；排期算法与原来完全一致。

```
仓库根（cwzjason/leetcode）
├── vercel.json          部署配置：静态输出目录指向 review
└── review/              网页代码
    ├── index.html           页面（登录 + 复习面板）
    ├── style.css            样式
    ├── app.js               逻辑（读 Supabase、排期计算、完成/撤销/新题）
    ├── config.js            ★ 唯一需要填配置的地方（Supabase 地址 + anon key）
    └── supabase/schema.sql  建表脚本（第一次在 Supabase 里执行一次）

> 说明：Supabase 的客户端库不放进仓库。浏览器打开网页时，
> 会自动从 CDN（unpkg）加载它再连接数据库，所以代码里干干净净。
> 如果某天 CDN 访问异常，把 `app.js` 顶部的 `SUPABASE_CDN` 换成
> `https://cdn.jsdelivr.net/npm/@supabase/supabase-js@2/dist/umd/supabase.js` 即可。
```

## 使用流程（按顺序）

### 1. Vercel 部署
1. 打开 [vercel.com](https://vercel.com) → 用 GitHub 登录 → **Add New Project**；
2. Import 仓库 `cwzjason/leetcode`；
3. Framework Preset 选 **Other**；Root Directory 保持默认（仓库根 `vercel.json` 已把输出目录指向 `review`）；
4. 直接 **Deploy**。部署成功后得到一个网址，如 `https://leetcode-xxx.vercel.app`。

### 2. Supabase 建库
1. 打开 [supabase.com](https://supabase.com) 注册 → **New Project**（地区可选 Singapore 附近）；
2. 项目建好后：左侧 **SQL Editor** → **New query** → 粘贴 `supabase/schema.sql` 全部内容 → **Run**（建表成功）；
3. 左侧 **Authentication → Providers**：确认 **Email** 是开启的；
4. **Authentication → Users → Add user**，输入你的邮箱和密码 —— 这就是网页的登录账号。

### 3. 填配置并重新部署
1. 回到 Supabase：**项目设置 Settings → API**，复制 **Project URL** 和 **anon public** key；
2. 打开本目录的 `config.js`，把两个占位符替换成真实值；
3. `git add -A && git commit && git push` → Vercel 检测到推送自动重新部署（等 1 分钟）；
4. 打开 Vercel 网址 → 用刚才建的账号登录 → 开始使用。

### 4. 日常使用
- 到期复习的题：点「完成第 X 轮复习」→ 自动排下一次；
- 点错可点「撤销」（再点一次确认）；
- 做了一道系统里没有的新题：顶部输入框填**题号**（如 `977`）或**题名**回车
  → 弹出小窗补全题目名/难度 → 加入排期（今天算新学，明天开始复习）；
- 未完成的题自动顺延，通关 7 轮后自动进入「已通关」。

## 数据安全
- 所有表开启了**行级安全（RLS）**：未登录看不到、登录后也只能读写你自己的数据；
- `config.js` 里的 anon key 是设计上公开的（浏览器必须带它），真正危险的是
  **service_role key —— 永远不要放进网页代码**；
- 想换设备/重装系统：数据都在 Supabase，与新页面无关。

## 常见问题
- **登录提示 "Invalid login credentials"**：账号要先去 Supabase
  Authentication → Users 创建（不是用邮箱去 Sign up）。
- **页面空白/读不到数据**：多半是 `config.js` 还是占位符，或 SQL 没执行成功。
- **Supabase 免费项目闲置 7 天会暂停**：只要坚持每天用（页面会请求数据库），就不会停；
  真停了去 Dashboard 点一下恢复即可。
