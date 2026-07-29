import Button from './Button';
import Card from './Card';

type SessionCardProps = {
  title: string;
  subtitle: string;
  userId: string;
  logoutAction: () => Promise<void>;
  // Not a style choice: whether this card carries the page's main heading
  // depends on what else is on the page. The auth layout renders it alone
  // (h1); the landing page renders it beside `Hero`, which owns the h1 (h2).
  headingLevel?: 'h1' | 'h2';
};

export default function SessionCard({
  title,
  subtitle,
  userId,
  logoutAction,
  headingLevel: Heading = 'h2',
}: SessionCardProps) {
  return (
    <Card>
      <Heading className="mb-1 text-2xl font-bold">{title}</Heading>
      <p className="text-on-elevated-surface/60 mb-6 text-sm">{subtitle}</p>
      <div className="flex flex-col gap-3">
        <Button href={`/${userId}`}>Go to my profile</Button>
        <form action={logoutAction}>
          <Button type="submit" variant="outline">
            Log out
          </Button>
        </form>
      </div>
    </Card>
  );
}
