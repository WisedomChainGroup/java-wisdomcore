#!/usr/bin/env node

/*
delete blocks starts from height h (inclusive)
删除（包含）某一高度以上的区块
示例
node delete-block.js --host localhost --port 5432 \
--username postgres --password postgres \
--database postgres --height 125852
 */

const pg = require('pg')
const program = require('commander')

program
    .option("--host <string>", "pg host")
    .option("--port <number>", "pg port")
    .option("--username <string>", "pg username")
    .option("--password <string>", "pg password")
    .option("--database <string>", "pg database")
    .option("--height <number>", "end block height")


program.parse(process.argv)

const config = {
    user: program.username | 'postgres',
    database: program.database | 'postgres',
    password: program.password | '',
    port: program.port | 5432,
    host: program.host | 'localhost'
}

const pool = new pg.Pool(config)


const HEIGHT = program.height

async function main() {
    const h = (await pool.query('select max(height) as height from header')).rows[0].height
    for (let i = h; i >= HEIGHT; i--) {
        const rows = (await pool.query(`select block_hash as hash from header where height = ${i}`)).rows
        for (let r of rows) {
            const rows = (await pool.query("select tx_hash as hash from transaction_index where tx_hash = $1", [r.hash])).rows
            for (let r of rows) {
                // 删掉事务
                await pool.query("delete from transaction where tx_hash = $1", [r.hash])
            }
            // 删掉 transaction index
            await pool.query("delete from transaction_index where block_hash = $1", [r.hash])
        }
        // 删掉 header
        await pool.query("delete from header where height = $1", [i])
    }
    // 删掉 incubator
    await pool.query("delete from incubator_state where height >= $1", [HEIGHT])
    await pool.query("delete from account where blockheight >= $1", [HEIGHT])
}

main().then(() => {
    console.log("success")
}).catch(console.error)
