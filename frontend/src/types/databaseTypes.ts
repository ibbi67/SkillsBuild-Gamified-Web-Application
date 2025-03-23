export interface Course {
    id: number;
    title: string;
    description: string;
    link: string;
    estimatedDuration: number;
    difficulty: number;
}

export interface Person {
    id: number;
    username: string;
    streak: number;
    lastLoginDate: string;
    favoriteCourses: Course[];
}

export interface Enrollment {
    id: number;
    course: Course;
    person: Person;
    timeSpent: number;
}