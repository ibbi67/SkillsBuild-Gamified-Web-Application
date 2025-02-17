export type LoginRequest = {
    username: string;
    password: string;
};
export type LoginResponse = null;

export type SignupRequest = {
    username: string;
    password: string;
};
export type SignupResponse = null;

export type MeRequest = null;
export type MeResponse = {
    id: number;
    username: string;
    password: string;
    roles: string[];
    authorities: string[];
    enabled: boolean;
    credentialsNonExpired: boolean;
    accountNonExpired: boolean;
    accountNonLocked: boolean;
    streak: Streak;
};

export type LogoutRequest = null;
export type LogoutResponse = null;
