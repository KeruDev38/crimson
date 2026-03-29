"use client";

import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { FormEvent, startTransition, useEffect, useMemo, useState } from "react";
import type {
  AccountDetails,
  CreateAccountPayload,
  PageResponse,
  TransactionItem,
  TransferPayload
} from "@/lib/types";

const defaultTransactions: TransactionItem[] = [];

const initialCreateForm: CreateAccountPayload = {
  customerId: "",
  currency: "MXN",
  initialBalance: 2500
};

const initialTransferForm: TransferPayload = {
  senderAccountId: "",
  receiverAccountId: "",
  amount: 150,
  currency: "MXN",
  reference: "Operations transfer"
};

export function CrimsonDashboard() {
  const searchParams = useSearchParams();
  const [accountId, setAccountId] = useState("");
  const [selectedAccountId, setSelectedAccountId] = useState("");
  const [account, setAccount] = useState<AccountDetails | null>(null);
  const [transactions, setTransactions] = useState<TransactionItem[]>(defaultTransactions);
  const [createForm, setCreateForm] = useState(initialCreateForm);
  const [transferForm, setTransferForm] = useState(initialTransferForm);
  const [loading, setLoading] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);
  const [transferLoading, setTransferLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [flash, setFlash] = useState<string | null>(null);

  const requestedAccountId = searchParams.get("account");

  useEffect(() => {
    if (requestedAccountId && requestedAccountId !== accountId) {
      setAccountId(requestedAccountId);
      void loadAccount(requestedAccountId);
    }
  }, [requestedAccountId]);

  const metrics = useMemo(() => {
    if (!account) {
      return {
        inflow: 0,
        outflow: 0,
        throughput: 0,
        averageTicket: 0
      };
    }

    const inflow = transactions
      .filter((item) => item.receiverAccountId === account.accountId)
      .reduce((sum, item) => sum + item.amount, 0);
    const outflow = transactions
      .filter((item) => item.senderAccountId === account.accountId)
      .reduce((sum, item) => sum + item.amount, 0);
    const throughput = inflow + outflow;

    return {
      inflow,
      outflow,
      throughput,
      averageTicket: transactions.length === 0 ? 0 : throughput / transactions.length
    };
  }, [account, transactions]);

  async function loadAccount(targetAccountId: string) {
    if (!targetAccountId.trim()) {
      setError("Enter an account id to inspect.");
      return;
    }

    setLoading(true);
    setError(null);
    setFlash(null);

    try {
      const [accountResponse, transactionsResponse] = await Promise.all([
        fetch(`/api/accounts/${targetAccountId.trim()}`, { cache: "no-store" }),
        fetch(`/api/accounts/${targetAccountId.trim()}/transactions?page=0&size=8`, {
          cache: "no-store"
        })
      ]);

      if (!accountResponse.ok) {
        const failure = await accountResponse.json();
        throw new Error(failure.message ?? "Unable to load account.");
      }

      if (!transactionsResponse.ok) {
        const failure = await transactionsResponse.json();
        throw new Error(failure.message ?? "Unable to load transactions.");
      }

      const nextAccount = normalizeAccount(await accountResponse.json());
      const history = normalizeTransactionPage(await transactionsResponse.json());

      startTransition(() => {
        setAccount(nextAccount);
        setTransactions(history.content);
        setSelectedAccountId(nextAccount.accountId);
        setTransferForm((current) => ({
          ...current,
          senderAccountId: current.senderAccountId || nextAccount.accountId,
          currency: nextAccount.currency
        }));
      });
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : "Unable to load account.");
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateAccount(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setCreateLoading(true);
    setError(null);
    setFlash(null);

    try {
      const response = await fetch("/api/accounts", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(createForm)
      });

      const payload = await response.json();
      if (!response.ok) {
        throw new Error(payload.message ?? "Unable to create account.");
      }

      const newAccountId = payload.accountId as string;
      setAccountId(newAccountId);
      setFlash(`Account ${newAccountId} created successfully.`);
      await loadAccount(newAccountId);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : "Unable to create account.");
    } finally {
      setCreateLoading(false);
    }
  }

  async function handleTransfer(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setTransferLoading(true);
    setError(null);
    setFlash(null);

    try {
      const response = await fetch("/api/transfers", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(transferForm)
      });

      const payload = await response.json();
      if (!response.ok) {
        throw new Error(payload.message ?? "Unable to transfer funds.");
      }

      setFlash(`Transfer ${payload.transactionId} accepted.`);
      if (selectedAccountId) {
        await loadAccount(selectedAccountId);
      }
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : "Unable to transfer funds.");
    } finally {
      setTransferLoading(false);
    }
  }

  return (
    <main className="shell">
      <section className="hero-panel">
        <div className="hero-copy">
          <span className="eyebrow">Crimson Console</span>
          <h1>Account intelligence for fast-moving banking operations.</h1>
          <p>
            Inspect live balances, track transaction velocity, and move funds from a single
            ruby-toned control room built on top of the Crimson APIs.
          </p>
          <Link href="/onboarding" className="inline-link">
            Start customer onboarding
          </Link>
        </div>

        <form
          className="lookup-panel"
          onSubmit={(event) => {
            event.preventDefault();
            void loadAccount(accountId);
          }}
        >
          <label htmlFor="account-id">Inspect account</label>
          <div className="inline-form">
            <input
              id="account-id"
              name="account-id"
              placeholder="Paste account UUID"
              value={accountId}
              onChange={(event) => setAccountId(event.target.value)}
            />
            <button type="submit" disabled={loading}>
              {loading ? "Loading..." : "Load metrics"}
            </button>
          </div>
          <p className="helper-text">
            The dashboard will pull live balances and the latest 8 transfers for the selected account.
          </p>
        </form>
      </section>

      {(error || flash) && (
        <section className={`status-banner ${error ? "error" : "success"}`}>
          {error ?? flash}
        </section>
      )}

      <section className="metrics-grid">
        <MetricCard
          label="Available balance"
          value={account ? formatMoney(account.balance, account.currency) : "--"}
          detail={account ? `Status ${account.status}` : "Load an account to begin"}
        />
        <MetricCard
          label="Incoming volume"
          value={account ? formatMoney(metrics.inflow, account.currency) : "--"}
          detail="Received across visible history"
        />
        <MetricCard
          label="Outgoing volume"
          value={account ? formatMoney(metrics.outflow, account.currency) : "--"}
          detail="Sent across visible history"
        />
        <MetricCard
          label="Average ticket"
          value={account ? formatMoney(metrics.averageTicket, account.currency) : "--"}
          detail="Average transfer size"
        />
      </section>

      <section className="workspace-grid">
        <article className="panel account-panel">
          <div className="panel-heading">
            <div>
              <span className="eyebrow">Account profile</span>
              <h2>{account ? account.customerFirstName + " " + account.customerLastName : "No account loaded"}</h2>
            </div>
            <div className="tech-pill">{account?.currency ?? "MXN"}</div>
          </div>

          <div className="profile-grid">
            <ProfileField label="Account id" value={account?.accountId ?? "Waiting for selection"} />
            <ProfileField label="Customer id" value={account?.customerId ?? "Waiting for selection"} />
            <ProfileField label="Owner email" value={account?.customerEmail ?? "Waiting for selection"} />
            <ProfileField
              label="Updated"
              value={account ? formatDateTime(account.updatedAt) : "Waiting for selection"}
            />
          </div>

          <div className="throughput-bar">
            <span style={{ width: `${account ? Math.min(100, Math.max(12, metrics.throughput / 50)) : 16}%` }} />
          </div>
          <p className="helper-text">
            Throughput reflects visible inflow plus outflow and helps spot unusually quiet or noisy accounts.
          </p>
        </article>

        <article className="panel form-panel">
          <div className="panel-heading">
            <div>
              <span className="eyebrow">Open account</span>
              <h2>Open an account for an existing customer</h2>
            </div>
          </div>

          <form className="stack-form" onSubmit={handleCreateAccount}>
            <div className="form-grid">
              <Field
                label="Customer id"
                value={createForm.customerId}
                onChange={(value) => setCreateForm((current) => ({ ...current, customerId: value }))}
              />
              <Field
                label="Currency"
                value={createForm.currency}
                onChange={(value) => setCreateForm((current) => ({ ...current, currency: value }))}
              />
              <Field
                label="Initial balance"
                type="number"
                step="0.01"
                value={String(createForm.initialBalance)}
                onChange={(value) =>
                  setCreateForm((current) => ({ ...current, initialBalance: Number(value || 0) }))
                }
              />
            </div>
            <p className="helper-text">
              Customer creation only happens in onboarding. Use that flow first, then come back here with the customer id.
            </p>
            <button type="submit" disabled={createLoading}>
              {createLoading ? "Creating..." : "Open account"}
            </button>
          </form>
        </article>

        <article className="panel form-panel">
          <div className="panel-heading">
            <div>
              <span className="eyebrow">Move funds</span>
              <h2>Launch a transfer</h2>
            </div>
          </div>

          <form className="stack-form" onSubmit={handleTransfer}>
            <div className="form-grid">
              <Field
                label="Sender account"
                value={transferForm.senderAccountId}
                onChange={(value) => setTransferForm((current) => ({ ...current, senderAccountId: value }))}
              />
              <Field
                label="Receiver account"
                value={transferForm.receiverAccountId}
                onChange={(value) => setTransferForm((current) => ({ ...current, receiverAccountId: value }))}
              />
              <Field
                label="Amount"
                type="number"
                step="0.01"
                value={String(transferForm.amount)}
                onChange={(value) =>
                  setTransferForm((current) => ({ ...current, amount: Number(value || 0) }))
                }
              />
              <Field
                label="Currency"
                value={transferForm.currency}
                onChange={(value) => setTransferForm((current) => ({ ...current, currency: value }))}
              />
              <Field
                label="Reference"
                value={transferForm.reference}
                onChange={(value) => setTransferForm((current) => ({ ...current, reference: value }))}
              />
            </div>
            <button type="submit" disabled={transferLoading}>
              {transferLoading ? "Sending..." : "Submit transfer"}
            </button>
          </form>
        </article>
      </section>

      <section className="panel">
        <div className="panel-heading">
          <div>
            <span className="eyebrow">Transaction stream</span>
            <h2>Recent account movement</h2>
          </div>
          <div className="tech-pill">{transactions.length} items</div>
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Direction</th>
                <th>Reference</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {transactions.length === 0 ? (
                <tr>
                  <td colSpan={5} className="empty-cell">
                    No transaction history yet. Create or load an account to populate this feed.
                  </td>
                </tr>
              ) : (
                transactions.map((transaction) => {
                  const outgoing = transaction.senderAccountId === selectedAccountId;
                  return (
                    <tr key={transaction.transactionId}>
                      <td>
                        <span className={`direction-tag ${outgoing ? "out" : "in"}`}>
                          {outgoing ? "Outflow" : "Inflow"}
                        </span>
                      </td>
                      <td>{transaction.reference || "Manual transfer"}</td>
                      <td>{account ? formatMoney(transaction.amount, account.currency) : transaction.amount}</td>
                      <td>{transaction.status}</td>
                      <td>{formatDateTime(transaction.createdAt)}</td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
}

function MetricCard({
  label,
  value,
  detail
}: {
  label: string;
  value: string;
  detail: string;
}) {
  return (
    <article className="metric-card">
      <span className="metric-label">{label}</span>
      <strong>{value}</strong>
      <p>{detail}</p>
    </article>
  );
}

function ProfileField({ label, value }: { label: string; value: string }) {
  return (
    <div className="profile-field">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function Field({
  label,
  value,
  onChange,
  type = "text",
  step
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  step?: string;
}) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} step={step} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function formatMoney(value: number, currency: string) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency
  }).format(value);
}

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat("en-US", {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(new Date(value));
}

function normalizeAccount(payload: AccountDetails | Record<string, unknown>): AccountDetails {
  const account = payload as Record<string, unknown>;

  return {
    accountId: String(account.accountId ?? ""),
    customerId: String(account.customerId ?? ""),
    customerFirstName: String(account.customerFirstName ?? ""),
    customerLastName: String(account.customerLastName ?? ""),
    customerEmail: String(account.customerEmail ?? ""),
    currency: String(account.currency ?? "MXN"),
    balance: Number(account.balance ?? 0),
    status: String(account.status ?? ""),
    createdAt: String(account.createdAt ?? ""),
    updatedAt: String(account.updatedAt ?? "")
  };
}

function normalizeTransactionPage(
  payload: PageResponse<TransactionItem> | Record<string, unknown>
): PageResponse<TransactionItem> {
  const page = payload as Record<string, unknown>;
  const content = Array.isArray(page.content) ? page.content : [];

  return {
    content: content.map((item) => normalizeTransaction(item as Record<string, unknown>)),
    page: Number(page.page ?? 0),
    size: Number(page.size ?? 0),
    totalElements: Number(page.totalElements ?? 0),
    totalPages: Number(page.totalPages ?? 0),
    first: Boolean(page.first),
    last: Boolean(page.last)
  };
}

function normalizeTransaction(payload: Record<string, unknown>): TransactionItem {
  return {
    transactionId: String(payload.transactionId ?? ""),
    senderAccountId: String(payload.senderAccountId ?? ""),
    receiverAccountId: String(payload.receiverAccountId ?? ""),
    amount: Number(payload.amount ?? 0),
    currency: String(payload.currency ?? "MXN"),
    status: String(payload.status ?? ""),
    reference: payload.reference == null ? "" : String(payload.reference),
    createdAt: String(payload.createdAt ?? "")
  };
}
