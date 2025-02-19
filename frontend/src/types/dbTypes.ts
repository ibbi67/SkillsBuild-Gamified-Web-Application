export interface Streak {
    id: number;
    streak: number;
    previousLogin: string | null;
}

export interface Course {
    id: number;
    title: string;
    description: string;
    link: string;
    estimatedDuration: string;
}

export interface Enrollment {
    id: number;
    progress: string;
    user: User;
    course: Course;
}

export interface LeaderboardEntry {
    id: number;
    user: User;
    points: number;
}

export interface User {
    id: number;
    username: string;
    password: string;
    roles: string[];
    favouriteCourses: Course[];
    streak: Streak;
    enrollments: Enrollment[];
    leaderboardEntry: LeaderboardEntry | null;
    authorities: string[];
    enabled: boolean;
    credentialsNonExpired: boolean;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
}
