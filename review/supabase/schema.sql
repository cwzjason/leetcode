-- ============================================================
-- LeetCode 复习系统 · Supabase 建表脚本（无邮箱登录版）
-- 用法：Supabase 控制台 → SQL Editor → New query → 粘贴本文件全部内容 → Run
--
-- 适用：全新项目第一次建表。
-- 如果你已经用旧版（带 user_id / 邮箱登录）脚本建过表，
-- 请改跑 migration-anon.sql 完成迁移。
--
-- 说明：本工具是个人单用户。网页端用「访问口令」当门禁，
--       数据库不再区分用户，所以表里没有 user_id，
--       也不开启 RLS（页面用 anon key 直接读写）。
-- ============================================================

-- 题目复习状态表
create table if not exists public.problems (
  key            text primary key,                    -- 唯一键：题号字符串 或 题目名
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
  interval   jsonb not null default '[1,2,4,7,15,30,60]'::jsonb,  -- 艾宾浩斯间隔（天）
  updated_at timestamptz not null default now()
);
