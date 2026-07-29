import Tag from './Tag';

const TAGS = [
  'Time tracking',
  'Task planning',
  'Progress tracking',
  'Stats',
  'Community',
];

// Fixed 42Hub brand statement - deliberately not wired to surface/primary
// theme tokens, so it stays put while the rest of the page re-themes.
export default function Hero() {
  return (
    <div className="bg-gradient-start-page max-w-md rounded-2xl px-10 py-12">
      <h1 className="text-brand-additional-color mb-4 text-4xl font-bold leading-tight">
        Every <span className="text-brand-secondary-color">skill</span> has a
        story –<br />
        start yours!
      </h1>
      <p className="text-brand-main-color mb-10 text-sm leading-relaxed">
        Turn your daily grind into a journey of mastery.
        <br />
        <span className="text-brand-secondary-color">42Hub</span> is a tool
        designed for high-achievers who want to bridge the gap between
        &quot;getting things done&quot; and &quot;getting better&quot;.
      </p>
      <div className="flex flex-wrap justify-center gap-2">
        {TAGS.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    </div>
  );
}
