import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";

export const metadata: Metadata = {
  title: "Crimson Console",
  description: "Ruby-toned transaction operations dashboard for Crimson Bank"
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <header className="topbar">
          <Link href="/" className="brandmark">
            Crimson Console
          </Link>
          <nav className="topnav">
            <Link href="/">Dashboard</Link>
            <Link href="/onboarding">Onboarding</Link>
          </nav>
        </header>
        {children}
      </body>
    </html>
  );
}
