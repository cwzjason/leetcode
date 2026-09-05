// ============================================================
// 云端版配置
// 创建 Supabase 项目后：Dashboard → 项目设置 Settings → API
// 把 "Project URL" 和 "anon public" 两个值填到下面，
// 保存后推到 GitHub（Vercel 会自动重新部署）。
// 注意：anon key 本来就会暴露在浏览器里，是安全的设计；
//       service_role key 才绝不能出现在网页代码里。
// ============================================================
window.APP_CONFIG = {
  supabaseUrl: "https://YOUR-PROJECT-REF.supabase.co",
  supabaseAnonKey: "YOUR-ANON-PUBLIC-KEY",
  interval: [1, 2, 4, 7, 15, 30, 60],   // 艾宾浩斯间隔（天）；首次建库用
};
