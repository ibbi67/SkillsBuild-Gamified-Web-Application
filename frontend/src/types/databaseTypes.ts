export interface Course {
    id: number;
    title: string;
    description: string;
    link: string;
    estimatedDuration: number;
    difficulty: number;
    views: number;
}

export interface Badge {
    id: number;
    name: string;
    description: string;
    imageUrl: string;
    criteriaType: string;
    criteriaValue: number;
}

export interface Person {
    id: number;
    username: string;
    streak: number;
    lastLoginDate: string;
    favoriteCourses: Course[];
    firstName: string;
    lastName: string;
    email: string;
    avatarLink: string;
    badges: Badge[];
}

export interface Enrollment {
    id: number;
    course: Course;
    person: Person;
    timeSpent: number;
    completed: boolean;
}

export interface Comment {
    id: number;
    content: string;
    createdAt: string;
    person: Person;
    course: Course;
}

export interface Goal {
    id: number;
    startDate: string;
    endDate: string;
    description: string;
    reward: string;
    person: Person;
    enrollments: Enrollment[];
}