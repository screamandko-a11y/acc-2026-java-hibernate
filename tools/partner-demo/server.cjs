const http = require("node:http");

const expectedToken = process.env.EXTERNAL_API_TOKEN;
const port = Number(process.env.PARTNER_API_PORT || 9090);

if (!expectedToken) {
    throw new Error("Set EXTERNAL_API_TOKEN before starting the server");
}

const server = http.createServer((request, response) => {
    response.setHeader("Content-Type", "application/json; charset=utf-8");

    if (request.method !== "GET" || request.url !== "/partner/status") {
        response.writeHead(404);
        response.end(JSON.stringify({ error: "Not found" }));
        return;
    }

    if (request.headers.authorization !== `Bearer ${expectedToken}`) {
        response.writeHead(401, {
            "WWW-Authenticate": "Bearer"
        });
        response.end(JSON.stringify({ error: "Invalid or missing token" }));
        return;
    }

    const mode = process.env.DEMO_API_MODE || "ok";

    if (mode === "forbidden") {
        response.writeHead(403);
        response.end(JSON.stringify({ error: "Access denied" }));
        return;
    }

    if (mode === "slow") {
        setTimeout(() => {
            response.writeHead(200);
            response.end(JSON.stringify({ status: "ok" }));
        }, 5000);
        return;
    }

    response.writeHead(200);
    response.end(JSON.stringify({ status: "ok" }));
});

server.listen(port, "127.0.0.1", () => {
    console.log(`Partner demo API: http://127.0.0.1:${server.address().port}`);
});
