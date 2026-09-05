-- ============================================================
-- LeetCode 复习系统 · Supabase 建表脚本
-- 用法：Supabase 控制台 → SQL Editor → New query → 粘贴本文件全部内容 → Run
-- 之后再到 Authentication → Users → Add user 创建你的登录账号
-- ============================================================

-- 题目复习状态表
create table if not exists public.problems (
  key            text primary key,                    -- 唯一键：题号字符串 或 题目名
  user_id        uuid not null default auth.uid(),    -- 归属用户（行级安全用）
  id             integer,                             -- 题号（纯数字时有值）
  name           text not null,                       -- 展示名
  level          text not null default 'easy',        -- easy / medium / hard
  added          date not null default current_date,  -- 首次做题日
  stage          integer not null default 0,          -- 已完成复习轮数
  completed      boolean not null default false,      -- 是否通关
  last_done      date,                                -- 最近一次完成日期
  next_review    date,                                -- 下次复习日期
  completed_date date,                                -- 通关日期
  history        jsonb not null default '[]'::jsonb,  -- 复习历史日期数组
  archived       boolean not null default false,
  snapshot       jsonb,                               -- 最近一次「完成」前的状态（撤销用）
  created_at     timestamptz not null default now()
);

-- 全局设置表（固定单行 id=1）
create table if not exists public.meta (
  id         integer primary key default 1 check (id = 1),
  user_id    uuid not null default auth.uid(),
  interval   jsonb not null default '[1,2,4,7,15,30,60]'::jsonb,  -- 艾宾浩斯间隔（天）
  updated_at timestamptz not null default now()
);

-- 行级安全：未登录(anon)完全不可见；登录用户只能读写自己的数据
alter table public.problems enable row level security;
alter table public.meta    enable row level security;

drop policy if exists "owner problems all" on public.problems;
create policy "owner problems all" on public.problems
  for all to authenticated
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

drop policy if exists "owner meta all" on public.meta;
create policy "owner meta all" on public.meta
  for all to authenticated
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);
