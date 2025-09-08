import { useEffect, useState } from "react";

export default function App() {
  const [msg, setMsg] = useState("loading...");

  useEffect(() => {
    const apiBase = (import.meta.env.VITE_API_BASE as string | undefined) || "/api";
    fetch(`${apiBase}/health`)
      .then(r => {
        if (!r.ok) throw new Error("non-ok");
        return r.json();
      })
      .then(d => setMsg(`${d.status} • ${d.service} v${d.version}`))
      .catch(() => setMsg("api unreachable"));
  }, []);

  return (
    <main style={{ fontFamily: "system-ui", padding: 24 }}>
      <h1>Cronoboard</h1>
      <p>Backend health: {msg}</p>
    </main>
  );
}
