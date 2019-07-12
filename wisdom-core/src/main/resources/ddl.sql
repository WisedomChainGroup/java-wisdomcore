create table if not exists transaction_index
(
    block_hash bytea not null,
    tx_hash bytea not null,
    tx_index integer,
    constraint transaction_index_pk
        primary key (block_hash, tx_hash)
);


create index if not exists transaction_index_block_hash
    on transaction_index (block_hash);

create table if not exists transaction
(
    version smallint,
    tx_hash bytea not null
        constraint transaction_pk
            primary key,
    type smallint,
    nonce bigint,
    "from" bytea,
    gas_price bigint,
    amount bigint,
    payload bytea,
    signature bytea,
    "to" bytea
);

create unique index if not exists transaction_tx_hash_uindex
    on transaction (tx_hash);

create table if not exists header
(
    block_hash bytea not null
        constraint header_pkey
            primary key,
    version bigint not null,
    hash_prev_block bytea not null,
    hash_merkle_root bytea not null,
    hash_merkle_state bytea not null,
    hash_merkle_incubate bytea,
        height bigint not null,
    created_at bigint not null,
    nbits bytea not null,
    nonce bytea not null,
    block_notice bytea,
    total_weight bigint default 0,
    is_canonical boolean default true
);


/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : PostgreSQL
 Source Server Version : 90611
 Source Host           : localhost:5432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90611
 File Encoding         : 65001

 Date: 04/07/2019 15:19:36
*/


DROP TABLE IF EXISTS "public"."account";
CREATE TABLE "public"."account" (
                                    "id" bytea NOT NULL,
                                    "blockheight" int4 NOT NULL,
                                    "pubkeyhash" bytea NOT NULL,
                                    "nonce" int8 NOT NULL,
                                    "balance" int8 NOT NULL,
                                    "incubatecost" int8,
                                    "mortgage" int8,
                                    "vote" int8 DEFAULT 0 NOT NULL
)
;
COMMENT ON COLUMN "public"."account"."id" IS '主键ID';
COMMENT ON COLUMN "public"."account"."blockheight" IS '区块高度';
COMMENT ON COLUMN "public"."account"."pubkeyhash" IS '地址的公钥哈希';
COMMENT ON COLUMN "public"."account"."nonce" IS 'nonce,防止重放攻击';
COMMENT ON COLUMN "public"."account"."balance" IS 'WDC余额';
COMMENT ON COLUMN "public"."account"."incubatecost" IS '孵化本金';
COMMENT ON COLUMN "public"."account"."mortgage" IS '抵押金额';
COMMENT ON COLUMN "public"."account"."vote" IS '投票数';
COMMENT ON TABLE "public"."account" IS '账户对象';

-- ----------------------------
-- Uniques structure for table account
-- ----------------------------
ALTER TABLE "public"."account" ADD CONSTRAINT "uq_utxo_id" UNIQUE ("id");

-- ----------------------------
-- Primary Key structure for table account
-- ----------------------------
ALTER TABLE "public"."account" ADD CONSTRAINT "pk_utxo" PRIMARY KEY ("id");


/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : PostgreSQL
 Source Server Version : 90611
 Source Host           : localhost:5432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90611
 File Encoding         : 65001

 Date: 04/07/2019 20:20:33
*/


-- ----------------------------
-- Table structure for incubator_state
-- ----------------------------
DROP TABLE IF EXISTS "public"."incubator_state";
CREATE TABLE "public"."incubator_state" (
                                            "id" bytea NOT NULL,
                                            "share_pubkeyhash" bytea,
                                            "pubkeyhash" bytea NOT NULL,
                                            "txid_issue" bytea NOT NULL,
                                            "height" int4 NOT NULL,
                                            "cost" int8 NOT NULL,
                                            "interest_amount" int8 NOT NULL,
                                            "share_amount" int8,
                                            "last_blockheight_interest" int4 NOT NULL,
                                            "last_blockheight_share" int4
)
;
COMMENT ON COLUMN "public"."incubator_state"."id" IS 'id txhash+height';
COMMENT ON COLUMN "public"."incubator_state"."share_pubkeyhash" IS '推荐者公钥哈希';
COMMENT ON COLUMN "public"."incubator_state"."pubkeyhash" IS '孵化者公钥哈希';
COMMENT ON COLUMN "public"."incubator_state"."txid_issue" IS '申请孵化事务哈希';
COMMENT ON COLUMN "public"."incubator_state"."height" IS '高度';
COMMENT ON COLUMN "public"."incubator_state"."cost" IS '本金';
COMMENT ON COLUMN "public"."incubator_state"."interest_amount" IS '利息余额';
COMMENT ON COLUMN "public"."incubator_state"."share_amount" IS '分享余额';
COMMENT ON COLUMN "public"."incubator_state"."last_blockheight_interest" IS '上次提取利息高度';
COMMENT ON COLUMN "public"."incubator_state"."last_blockheight_share" IS '上次提取分享收益高度';
COMMENT ON TABLE "public"."incubator_state" IS '孵化器状态';

-- ----------------------------
-- Uniques structure for table incubator_state
-- ----------------------------
ALTER TABLE "public"."incubator_state" ADD CONSTRAINT "uq_incubator_state_id" UNIQUE ("id");

-- ----------------------------
-- Primary Key structure for table incubator_state
-- ----------------------------
ALTER TABLE "public"."incubator_state" ADD CONSTRAINT "pk_incubator_state" PRIMARY KEY ("id");

