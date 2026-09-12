import AccentLink from '../../components/AccentLink';
import Card from '../../components/Card';

export default function GamesHubPage() {
  return (
    <div className="flex flex-1 flex-col items-center justify-center p-4">
      <Card>
        <h1 className="mb-1 text-2xl font-extrabold">Games Hub</h1>
        <p className="mb-6 text-sm font-medium opacity-60">
          Choose a game to play!
        </p>

        <div className="flex flex-col gap-4">
          <div className="border-hub-border/50 hover:border-hub-border rounded-lg border p-4 transition-colors">
            <h2 className="mb-1 text-lg font-bold">2048</h2>
            <p className="mb-3 text-sm opacity-60">
              Join the numbers and get to the 2048 tile!
            </p>
            <AccentLink href="/games/2048">Play 2048</AccentLink>
          </div>
        </div>
      </Card>
    </div>
  );
}
