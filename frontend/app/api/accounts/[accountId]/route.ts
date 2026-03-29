import { proxyJson } from "@/lib/backend";

export async function GET(
  _request: Request,
  { params }: { params: Promise<{ accountId: string }> }
) {
  const { accountId } = await params;
  return proxyJson(`/api/v1/accounts/${accountId}`);
}
