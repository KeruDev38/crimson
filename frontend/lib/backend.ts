const baseUrl = process.env.INTERNAL_API_BASE_URL ?? "http://localhost:8080";

export async function proxyJson(path: string, init?: RequestInit) {
  const response = await fetch(`${baseUrl}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {})
    },
    cache: "no-store"
  });

  const bodyText = await response.text();
  const body = bodyText ? tryParseJson(bodyText) : null;

  return Response.json(body, { status: response.status });
}

function tryParseJson(bodyText: string) {
  try {
    return JSON.parse(bodyText);
  } catch {
    return { message: bodyText };
  }
}
