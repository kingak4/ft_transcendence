import Link from "next/link";

export default function Home() {
  return (
    <div className="flex flex-col items-center">
      <h1 className="text-6xl">Web app name.</h1>
      <Link className='border p-10 rounded-md' href={"/login"}>{"login page"}</Link>
    </div>
  );
}
