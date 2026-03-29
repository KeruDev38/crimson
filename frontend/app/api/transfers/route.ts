import { proxyJson } from "@/lib/backend";

export async function POST(request: Request) {
  return proxyJson("/api/v1/transfers", {
    method: "POST",
    body: await request.text()
  });
}
