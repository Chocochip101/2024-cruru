import { useQuery } from '@tanstack/react-query';

import type { Process } from '@customTypes/process';

import { DASHBOARD_ID } from '@constants/constants';
import QUERY_KEYS from '@hooks/queryKeys';
import processApis from '@api/process';

interface SimpleProcess {
  processName: string;
  processId: number;
}

interface UseProcessReturn {
  processes: Process[];
  processList: SimpleProcess[];
  error: Error | null;
  isLoading: boolean;
}

export default function useProcess(): UseProcessReturn {
  const { data, error, isLoading } = useQuery<{ processes: Process[] }>({
    queryKey: [QUERY_KEYS.DASHBOARD, DASHBOARD_ID],
    queryFn: () => processApis.get({ id: DASHBOARD_ID }),
  });

  const processes = data?.processes || [];

  const processList = processes.map((p) => ({ processName: p.name, processId: p.processId }));

  return {
    processes,
    processList,
    error,
    isLoading,
  };
}