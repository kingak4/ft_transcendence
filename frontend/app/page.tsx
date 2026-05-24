import Link from 'next/link';

export default function Home() {
  return (
    <div className="flex flex-col items-center">
      <h1 className="text-6xl">Web app name.</h1>
      <Link className="rounded-md border p-10" href={'/login'}>
        {'login page'}
      </Link>
    </div>
  );
}
