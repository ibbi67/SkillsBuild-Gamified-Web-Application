"use client";

import { useApi } from "@/hooks/useApi";
import { useEffect, useState } from "react";
import { LoginRequest } from "@/types/auth";
import Logo from "@/components/Logo";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/providers/AuthProvider";

export default function LoginPage() {
    const router = useRouter();
    const { setIsAuthenticated } = useAuth();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const { isLoading, isError, message, fetchData } = useApi<LoginRequest, LoginRequest>(
        "auth/login",
        { method: "POST", data: { username, password } },
    );

    const isLoginSucessful = message === "Login successful";
    const isInvalidCredentials = message === "Invalid credentials" || message === "User not found";

    useEffect(() => {
        if (isLoginSucessful) {
            setIsAuthenticated(true);
            router.push("/");
        }
    }, [isLoginSucessful, router, setIsAuthenticated]);

    return (
        <div className="flex justify-center items-center h-screen">
            <div className="flex flex-col p-4 bg-white rounded-lg shadow-lg gap-4 grow max-w-96">
                <Logo />
                <h1 className="font-bold text-center md:text-2xl">Login</h1>

                <input
                    type="text"
                    placeholder="Username"
                    className={`py-1 px-2 md:py-2 border border-gray-300 rounded-lg ${
                        isInvalidCredentials && "border-red-500"
                    }`}
                    onChange={(e) => setUsername(e.target.value)}
                />

                <input
                    type="password"
                    placeholder="Password"
                    className={`py-1 px-2 md:py-2 border border-gray-300 rounded-lg ${
                        isInvalidCredentials && "border-red-500"
                    }`}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button
                    className="bg-blue-500 text-white p-2 rounded-lg disabled:bg-blue-300"
                    onClick={fetchData}
                    disabled={isLoading}
                >
                    {isLoading ? "Logging in..." : "Confirm"}
                </button>

                {isError && message && <div className="text-red-500 text-sm mb-2">{message}</div>}

                <Link href="/auth/signup" className="text-blue-500 text-center">
                    Don&apos;t have an account? Sign up here!
                </Link>
            </div>
        </div>
    );
}
