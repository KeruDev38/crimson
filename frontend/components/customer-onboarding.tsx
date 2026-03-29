"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import type { CreateAccountPayload, CreateCustomerPayload, CustomerItem } from "@/lib/types";

const initialCustomerForm: CreateCustomerPayload = {
  firstName: "Mila",
  lastName: "Navarro",
  email: "mila.navarro@crimson.test"
};

const initialAccountForm: CreateAccountPayload = {
  customerId: "",
  currency: "MXN",
  initialBalance: 600
};

export function CustomerOnboarding() {
  const [customerForm, setCustomerForm] = useState(initialCustomerForm);
  const [accountForm, setAccountForm] = useState(initialAccountForm);
  const [customer, setCustomer] = useState<CustomerItem | null>(null);
  const [accountId, setAccountId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [flash, setFlash] = useState<string | null>(null);
  const [customerLoading, setCustomerLoading] = useState(false);
  const [accountLoading, setAccountLoading] = useState(false);

  async function handleCreateCustomer(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setCustomerLoading(true);
    setError(null);
    setFlash(null);

    try {
      const response = await fetch("/api/customers", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(customerForm)
      });
      const payload = await response.json();
      if (!response.ok) {
        throw new Error(payload.message ?? "Unable to create customer.");
      }

      setCustomer(payload);
      setAccountForm((current) => ({ ...current, customerId: payload.customerId }));
      setFlash(`Customer ${payload.customerId} created. You can open the account now.`);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : "Unable to create customer.");
    } finally {
      setCustomerLoading(false);
    }
  }

  async function handleCreateAccount(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setAccountLoading(true);
    setError(null);
    setFlash(null);

    try {
      const response = await fetch("/api/accounts", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(accountForm)
      });
      const payload = await response.json();
      if (!response.ok) {
        throw new Error(payload.message ?? "Unable to open account.");
      }

      setAccountId(payload.accountId);
      setFlash(`Account ${payload.accountId} opened successfully.`);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : "Unable to open account.");
    } finally {
      setAccountLoading(false);
    }
  }

  return (
    <main className="shell">
      <section className="hero-panel onboarding-hero">
        <div className="hero-copy">
          <span className="eyebrow">Customer onboarding</span>
          <h1>Create the user first, then open the account.</h1>
          <p>
            This flow mirrors a more realistic banking process: register the customer profile,
            issue the internal id, and then provision the financial account.
          </p>
        </div>
        <div className="lookup-panel timeline-panel">
          <div className="timeline-step active">
            <strong>01</strong>
            <div>
              <h3>Create customer</h3>
              <p>Persist the profile and secure a unique customer identifier.</p>
            </div>
          </div>
          <div className="timeline-step">
            <strong>02</strong>
            <div>
              <h3>Open account</h3>
              <p>Attach a balance-bearing account to the customer you just created.</p>
            </div>
          </div>
        </div>
      </section>

      {(error || flash) && (
        <section className={`status-banner ${error ? "error" : "success"}`}>
          {error ?? flash}
        </section>
      )}

      <section className="workspace-grid onboarding-grid">
        <article className="panel form-panel">
          <div className="panel-heading">
            <div>
              <span className="eyebrow">Step one</span>
              <h2>Create customer</h2>
            </div>
          </div>

          <form className="stack-form" onSubmit={handleCreateCustomer}>
            <div className="form-grid">
              <Field label="First name" value={customerForm.firstName} onChange={(value) => setCustomerForm((current) => ({ ...current, firstName: value }))} />
              <Field label="Last name" value={customerForm.lastName} onChange={(value) => setCustomerForm((current) => ({ ...current, lastName: value }))} />
              <Field label="Email" type="email" value={customerForm.email} onChange={(value) => setCustomerForm((current) => ({ ...current, email: value }))} />
            </div>
            <button type="submit" disabled={customerLoading}>
              {customerLoading ? "Creating..." : "Create customer"}
            </button>
          </form>
        </article>

        <article className="panel form-panel">
          <div className="panel-heading">
            <div>
              <span className="eyebrow">Step two</span>
              <h2>Open account</h2>
            </div>
            <div className="tech-pill">{accountForm.currency}</div>
          </div>

          <form className="stack-form" onSubmit={handleCreateAccount}>
            <div className="form-grid">
              <Field label="Customer id" value={accountForm.customerId} onChange={(value) => setAccountForm((current) => ({ ...current, customerId: value }))} />
              <Field label="Currency" value={accountForm.currency} onChange={(value) => setAccountForm((current) => ({ ...current, currency: value }))} />
              <Field label="Initial balance" type="number" step="0.01" value={String(accountForm.initialBalance)} onChange={(value) => setAccountForm((current) => ({ ...current, initialBalance: Number(value || 0) }))} />
            </div>
            <button type="submit" disabled={accountLoading}>
              {accountLoading ? "Opening..." : "Open account"}
            </button>
          </form>
        </article>

        <article className="panel account-panel">
          <div className="panel-heading">
            <div>
              <span className="eyebrow">Result</span>
              <h2>Provisioning summary</h2>
            </div>
          </div>

          <div className="profile-grid">
            <ProfileField label="Customer id" value={customer?.customerId ?? "Not created yet"} />
            <ProfileField label="Customer email" value={customer?.email ?? "Not created yet"} />
            <ProfileField label="Account id" value={accountId ?? "Not opened yet"} />
            <ProfileField label="Next step" value={accountId ? "Open dashboard and inspect metrics" : "Finish both onboarding steps"} />
          </div>

          <Link href={accountId ? `/?account=${accountId}` : "/"} className="inline-link">
            Go to operations dashboard
          </Link>
        </article>
      </section>
    </main>
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

function ProfileField({ label, value }: { label: string; value: string }) {
  return (
    <div className="profile-field">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
