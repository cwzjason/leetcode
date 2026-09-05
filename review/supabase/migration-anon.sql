-- ============================================================
-- 迁移：去掉邮箱登录 → 允许匿名(anon)直接读写
-- 适用：已经用旧版脚本（带 user_id + RLS + 仅 authenticated）
--       建过表的数据库。
-- 用法：Supabase 控制台 → SQL Editor → New query → 粘贴执行一次
-- ============================================================

-- 1) 关闭行级安全：页面用 anon key 直接读写（个人单用户工具）
alter table public.problems disable row level security;
alter table public.meta    disable row level security;

-- 2) user_id 列不再使用。若仍为 NOT NULL，anon 插入时 auth.uid()
--    为空会违反约束，所以放宽为可空（不删列，兼容已有数据）。
alter table public.problems alter column user_id drop not null;
alter table public.meta    alter column user_id drop not null;
