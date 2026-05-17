import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  output: 'standalone',
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: "http://backend:5000/:path*",
      },
    ];
  },
  reactCompiler: true,
};

export default nextConfig;
