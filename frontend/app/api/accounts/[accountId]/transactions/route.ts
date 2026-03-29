import { proxyJson } from "@/lib/backend";

export async function GET(
  request: Request,
  { params }: { params: Promise<{ accountId: string }> }
) {
  const { accountId } = await params;
  const { searchParams } = new URL(request.url);
  const page = searchParams.get("page") ?? "0";
  const size = searchParams.get("size") ?? "20";

  return proxyJson(`/api/v1/accounts/${accountId}/transactions?page=${page}&size=${size}`);
}
