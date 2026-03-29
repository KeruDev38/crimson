import { Suspense } from "react";
import { CrimsonDashboard } from "@/components/crimson-dashboard";

export default function HomePage() {
  return (
    <Suspense>
      <CrimsonDashboard />
    </Suspense>
  );
}
