export type LoginRequest = {
    username: string;
    password: string;
};

export type LoginResponse = string;

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
};
