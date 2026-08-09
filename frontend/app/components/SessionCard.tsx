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
    // The export has no card like this - its landing goes straight to the login
    // form, with no "welcome back, continue" state - so nothing here is read
    // from the design. Every value comes from the dictionary (12.0) instead:
    // the heading takes the "nagłówek karty" row (20px/800, was 24px/700), the
    // subtitle the "tekst drugorzędny" row, which asks for a weight this was
    // not setting, and the button stack takes the 16px the export uses between
    // stacked form controls.
    //
    // The trailing `mb-4` on the second button is gone. Inside a gap container
    // it added 16px below the last child, on top of the card's own padding -
    // the card already owns the space between its content and its edge.
    <Card>
      <Heading className="mb-1 text-xl font-extrabold">{title}</Heading>
      <p className="text-on-elevated-surface/60 mb-6 text-sm font-medium">
        {subtitle}
      </p>
      <div className="flex flex-col gap-4">
        <Button href={`/${userId}`} fullWidth>
          Go to my profile
        </Button>
        <form action={logoutAction}>
          <Button type="submit" variant="outline" fullWidth>
            Log out
          </Button>
        </form>
      </div>
    </Card>
  );
}
