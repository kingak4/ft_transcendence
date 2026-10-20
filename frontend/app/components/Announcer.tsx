export interface Announcement {
  id: string;
  text: string;
}

interface Props {
  announcement: Announcement | null;
}

export default function Announcer({ announcement }: Props) {
  return (
    <div aria-live="polite" className="sr-only">
      {announcement && <p key={announcement.id}>{announcement.text}</p>}
    </div>
  );
}
