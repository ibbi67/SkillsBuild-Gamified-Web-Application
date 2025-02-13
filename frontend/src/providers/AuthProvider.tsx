"use client";

import React, { createContext, useContext, useEffect, useState } from "react";
import { useApi } from "@/hooks/useApi";
import { MeRequest, MeResponse } from "@/types/auth";

interface AuthProviderProps {
    children: React.ReactNode;
}

interface AuthContextProps {
    isAuthenticated: boolean;
    setIsAuthenticated: (isAuthenticated: boolean) => void;
}

const AuthContext = createContext<AuthContextProps>({
    isAuthenticated: false,
    setIsAuthenticated: () => {},
});

export function AuthProvider({ children }: AuthProviderProps) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const { status, fetchData } = useApi<MeResponse, MeRequest>("auth/me");

    useEffect(() => {
        const checkAuth = async () => {
            try {
                await fetchData();
                setIsAuthenticated(status === 200);
            } catch (error) {
                console.error("Error checking authentication status: " + error);
                setIsAuthenticated(false);
            } finally {
                setIsLoading(false);
            }
        };

        checkAuth();
    }, []);

    if (isLoading) return null;

    return (
        <AuthContext.Provider value={{ isAuthenticated, setIsAuthenticated }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
