import React from 'react';
import { useGoalDashboard } from '@/queries/goals/useGoalDashboard';
import { GoalProgressDTO } from '@/types/goalTypes';
import GoalDashboardCard from './GoalDashboardCard';
import Link from 'next/link';

const DashboardGoalsSection = () => {
  const { data, isLoading, isError } = useGoalDashboard();
  
  if (isLoading) {
    return <div className="text-gray-500">Loading goals...</div>;
  }
  
  if (isError) {
    return <div className="text-red-500">Error loading goals</div>;
  }
  
  // Handle both response formats - direct array or wrapped response
  let goalsData: GoalProgressDTO[] = [];
  
  if (Array.isArray(data)) {
    // Backend is returning array directly
    goalsData = data;
  } else if (data && typeof data === 'object') {
    // Check for data property in a type-safe way
    if ('data' in data && Array.isArray((data as any).data)) {
      goalsData = (data as any).data;
    }
  }
  
  return (
    <div className="rounded-lg border p-4 shadow">
      <div className="flex items-center justify-between mb-2">
        <h2 className="font-bold">Your Goals</h2>
        <Link href="/goals">
          <button className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm">
            Add Goal
          </button>
        </Link>
      </div>
      
      <div className="space-y-3">
        {!goalsData || goalsData.length === 0 ? (
          <div className="text-gray-500">No goals found. Create some goals to get started!</div>
        ) : (
          goalsData.map((goal) => (
            <GoalDashboardCard key={goal.id} goal={goal} />
          ))
        )}
      </div>
    </div>
  );
};

export default DashboardGoalsSection;