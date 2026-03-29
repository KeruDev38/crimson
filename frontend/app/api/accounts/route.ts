import { proxyJson } from "@/lib/backend";

export async function POST(request: Request) {
  return proxyJson("/api/v1/accounts", {
    method: "POST",
    body: await request.text()
  });
}
