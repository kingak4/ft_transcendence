# Infrastructure Developer Guide

### Management Commands

**Bring up all infrastructure (detached)**
```bash
make up
```

**Stop all infrastructure**
```bash
make down
```

**View aggregated logs**
```bash
make logs
```

**Wipe existing database volumes & stop services**
```bash
make clean
```

## Service Connections

- **PostgreSQL**: `localhost:5432` 
- **Redis**: `localhost:6380`
