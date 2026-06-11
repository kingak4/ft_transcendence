
export default async function HomePage() {
  // try {

  //   const { data, error, response } = await client.GET("/hello/user");
  //   if (!response.ok) {
  //     return <div>Błąd serwera: {response.status} {response.headers}</div>;
  //   }

  //   return (
  //     <div className='flex flex-col w-screen min-h-screen justify-center items-center'>
  //       <h1 className='font-bold text-3xl'>HOME</h1>
  //       <label className="mt-4">{response.status}</label>
  //     </div>
  //   );
  // } catch (error: any) {
  return <div>Błąd połączenia</div>;
  // }
}
