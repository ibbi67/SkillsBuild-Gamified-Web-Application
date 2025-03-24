import { useState } from "react";
import { useCourses } from "@/queries/courses/useCourses";
import { useCreateGoal } from "@/queries/goals/useCreateGoal";
import { useMe } from "@/queries/auth/useMe";
import toast from "react-hot-toast";

export default function CreateGoalForm({ onClose }: { onClose: () => void }) {
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState(new Date().toISOString().split("T")[0]);
  const [endDate, setEndDate] = useState("");
  const [reward, setReward] = useState("");
  const [selectedCourses, setSelectedCourses] = useState<number[]>([]);
  
  const { data: coursesData, isLoading: isLoadingCourses } = useCourses();
  const { data: userData } = useMe();
  const { mutate: createGoal, isPending, isError, error } = useCreateGoal();
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!description || !endDate || !reward || selectedCourses.length === 0) {
      toast.error("Please fill out all fields and select at least one course");
      return;
    }
    
    // Validate end date is after start date
    if (new Date(endDate) <= new Date(startDate)) {
      toast.error("End date must be after start date");
      return;
    }
    
    createGoal({
      description,
      startDate,
      endDate,
      reward,
      courseIds: selectedCourses
    }, {
      onSuccess: () => {
        toast.success("Goal created successfully!");
        onClose();
      },
      onError: (err) => {
        toast.error(`Error creating goal: ${err.message}`);
      }
    });
  };
  
  const handleCourseToggle = (courseId: number) => {
    if (selectedCourses.includes(courseId)) {
      setSelectedCourses(selectedCourses.filter(id => id !== courseId));
    } else {
      setSelectedCourses([...selectedCourses, courseId]);
    }
  };
  
  if (isLoadingCourses) {
    return <div className="p-4">Loading courses...</div>;
  }
  
  return (
    <form onSubmit={handleSubmit} className="p-4 space-y-4">
      <div>
        <label className="block text-sm font-medium mb-1">Goal Description</label>
        <input
          type="text"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="w-full p-2 border rounded"
          placeholder="e.g., Complete React fundamentals"
          required
        />
      </div>
      
      <div className="flex gap-4">
        <div className="flex-1">
          <label className="block text-sm font-medium mb-1">Start Date</label>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="w-full p-2 border rounded"
            required
          />
        </div>
        <div className="flex-1">
          <label className="block text-sm font-medium mb-1">Target Completion Date</label>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="w-full p-2 border rounded"
            required
          />
        </div>
      </div>
      
      <div>
        <label className="block text-sm font-medium mb-1">Reward (what will you get when you complete this goal?)</label>
        <input
          type="text"
          value={reward}
          onChange={(e) => setReward(e.target.value)}
          className="w-full p-2 border rounded"
          placeholder="e.g., Weekend trip, new book, etc."
          required
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium mb-2">Select Courses to Include</label>
        <div className="max-h-60 overflow-y-auto border rounded p-2">
          {coursesData?.data.map(course => (
            <div key={course.id} className="flex items-center space-x-2 py-2 border-b">
              <input
                type="checkbox"
                id={`course-${course.id}`}
                checked={selectedCourses.includes(course.id)}
                onChange={() => handleCourseToggle(course.id)}
                className="h-4 w-4"
              />
              <label htmlFor={`course-${course.id}`} className="flex-1 cursor-pointer">
                <div className="font-medium">{course.title}</div>
                <div className="text-xs text-gray-500">Est. duration: {course.estimatedDuration}h</div>
              </label>
            </div>
          ))}
        </div>
        {selectedCourses.length === 0 && (
          <p className="text-xs text-red-500 mt-1">Please select at least one course</p>
        )}
      </div>
      
      <div className="flex justify-end space-x-2 pt-4">
        <button
          type="button"
          onClick={onClose}
          className="px-4 py-2 border rounded"
        >
          Cancel
        </button>
        <button
          type="submit"
          className="px-4 py-2 bg-blue-500 text-white rounded"
          disabled={isPending}
        >
          {isPending ? "Creating..." : "Create Goal"}
        </button>
      </div>
    </form>
  );
}