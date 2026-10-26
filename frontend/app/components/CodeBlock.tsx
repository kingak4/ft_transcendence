// Code is always LTR, even inside an RTL (Arabic) page. Long lines wrap instead
// of scrolling, so the block reflows at 320px without needing keyboard focus.
export default function CodeBlock({ children }: { children: string }) {
  return (
    <pre
      dir="ltr"
      className="bg-sunken-surface text-on-elevated-surface whitespace-pre-wrap rounded-xl p-4 text-start font-mono text-sm leading-relaxed [overflow-wrap:anywhere]"
    >
      <code>{children}</code>
    </pre>
  );
}
