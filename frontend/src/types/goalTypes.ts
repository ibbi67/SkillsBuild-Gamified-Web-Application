export interface Goal {
  id: number;
  startDate: string;
  endDate: string;
  description: string;
  reward: string;
  achieved: boolean;
  courses: Record<number, boolean>; // key: courseId, value: completed
  person_id: number;
}

export interface GoalProgressDTO {
  id: number;
  description: string;
  startDate: string;
  endDate: string;
  reward: string;
  progress: number; // Progress percentage (0-100)
  courses: Record<number, boolean>; // Course IDs and their completion status
}

export interface CreateGoalRequest {
  description: string;
  startDate: string;
  endDate: string;
  reward: string;
  courseIds: number[];
}

export interface UpdateCourseStatusRequest {
  goalId: number;
  courseId: number;
  completed: boolean;
}

export interface AddCoursesToGoalRequest {
  goalId: number;
  courses: Record<number, boolean>;
}

export interface GoalResponse {
  message: string;
  data: Goal;
}

export interface GoalsResponse {
  message: string;
  data: Goal[];
}

export interface GoalProgressResponse {
  message: string;
  data: GoalProgressDTO[];
}