// ============================================================
// 云端版配置
// 创建 Supabase 项目后：Dashboard → 项目设置 Settings → API
// 把 "Project URL" 和 "anon public" 两个值填到下面，
// 保存后推到 GitHub（Vercel 会自动重新部署）。
// 注意：anon key 本来就会暴露在浏览器里，是安全的设计；
//       service_role key 才绝不能出现在网页代码里。
// ============================================================
window.APP_CONFIG = {
  supabaseUrl: "https://ccjivjijjhfeaqgqmkbd.supabase.co",
  supabaseAnonKey: "sb_publishable_h__eOAKfBIM3KzSofjQQeg_uv1EcbxS",
  accessCode: "cwz",                 // 打开网页要输入的访问口令，想换就改这里
  interval: [1, 2, 4, 7, 15, 30, 60],   // 艾宾浩斯间隔（天）；首次建库用
  leetcodeBase: "https://leetcode.cn", // 打开原题用的站点（国内默认力扣；国际站可改 https://leetcode.com）
};
