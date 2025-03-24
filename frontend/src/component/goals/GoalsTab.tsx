import { useState } from "react";
import { useGoalDashboard } from "@/queries/goals/useGoalDashboard";
import CreateGoalForm from "@/component/goals/CreateGoalForm";
import GoalDashboardCard from "@/component/goals/GoalDashboardCard";
import { Plus } from "lucide-react";
import { useCourses } from "@/queries/courses/useCourses";

export function GoalsTab() {
  const [showCreateForm, setShowCreateForm] = useState(false);
  const { data: goalsData, isLoading, isError } = useGoalDashboard();
  
  if (isLoading) return <p>Loading goals...</p>;
  if (isError) return <p>Error fetching goals.</p>;
  
  const goals = goalsData?.data || [];
  
  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <button
          onClick={() => setShowCreateForm(true)}
          className="px-4 py-2 bg-blue-500 text-white rounded-lg flex items-center"
        >
          <Plus size={18} className="mr-1" />
          Create Goal
        </button>
      </div>
      
      {showCreateForm ? (
        <div className="mb-6 border rounded-lg shadow-lg">
          <div className="bg-blue-500 text-white font-medium p-3 rounded-t-lg">
            Create New Learning Goal
          </div>
          <CreateGoalForm onClose={() => setShowCreateForm(false)} />
        </div>
      ) : null}
      
      {goals.length === 0 ? (
        <div className="text-center py-8">
          <p className="text-gray-500 mb-4">You don't have any active goals yet.</p>
          <button
            onClick={() => setShowCreateForm(true)}
            className="px-4 py-2 bg-blue-500 text-white rounded-lg"
          >
            Create Your First Goal
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {goals.map(goal => (
            <GoalDashboardCard key={goal.id} goal={goal} />
          ))}
        </div>
      )}
    </div>
  );
}