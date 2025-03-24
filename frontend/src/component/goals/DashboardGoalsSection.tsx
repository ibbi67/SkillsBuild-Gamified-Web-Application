import { useGoalDashboard } from "@/queries/goals/useGoalDashboard";
import Link from "next/link";
import { ChevronRight } from "lucide-react";

export default function DashboardGoalsSection() {
    const { data: goalsData, isLoading, isError } = useGoalDashboard();
    
    if (isLoading) return <p className="text-sm">Loading goals...</p>;
    if (isError) {
      // More specific error handling
      return (
        <div className="rounded-lg border p-4 shadow">
          <h2 className="font-bold">Your Learning Goals</h2>
          <p className="text-sm text-gray-500 mt-2">Goals not available at the moment.</p>
        </div>
      );
    }
  
  const goals = goalsData?.data || [];
  
  // Sort goals by progress (most complete first)
  const sortedGoals = [...goals].sort((a, b) => b.progress - a.progress);
  
  // Take only the top 3 goals for the dashboard
  const topGoals = sortedGoals.slice(0, 3);
  
  return (
    <div className="rounded-lg border p-4 shadow">
      <div className="flex justify-between items-center mb-4">
        <h2 className="font-bold">Your Learning Goals</h2>
        <Link 
          href="/goals"
          className="text-blue-500 flex items-center text-sm"
        >
          See all <ChevronRight size={16} />
        </Link>
      </div>
      
      {topGoals.length === 0 ? (
        <div className="text-center py-4">
          <p className="text-gray-500 text-sm mb-2">No goals set yet</p>
          <Link
            href="/goals"
            className="px-3 py-1 bg-blue-500 text-white text-sm rounded inline-block"
          >
            Create Goal
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {topGoals.map(goal => (
            <div key={goal.id} className="border rounded p-3">
              <div className="flex justify-between items-start mb-2">
                <h3 className="font-medium">{goal.description}</h3>
                <span className="text-sm text-blue-500">{goal.progress.toFixed(0)}%</span>
              </div>
              
              <div className="w-full bg-gray-200 rounded-full h-1.5 mb-2">
                <div
                  className="bg-blue-600 h-1.5 rounded-full"
                  style={{ width: `${goal.progress}%` }}
                ></div>
              </div>
              
              <div className="flex justify-between text-xs text-gray-500">
                <span>
                  Courses: {Object.values(goal.courses).filter(complete => complete).length} / {Object.keys(goal.courses).length} completed
                </span>
                <span>
                  Due by: {new Date(goal.endDate).toLocaleDateString()}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
