export interface Course {
    id: number;
    title: string;
    description: string;
    link: string;
    estimatedDuration: number;
    difficulty: number;
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
}

export interface Comment {
    id: number;
    content: string;
    createdAt: string;
    person: {
        id: number;
        username: string;
    };
}