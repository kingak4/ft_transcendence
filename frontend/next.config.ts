import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  output: 'standalone',
  async rewrites() {
    if (process.env.NODE_ENV === 'development') {
      return [
        {
          source: '/api/:path*',
          destination: `${process.env.BACKEND_URL}/:path*`,
        },
      ];
    }
    return [];
  },
  reactCompiler: true,
};

export default nextConfig;
