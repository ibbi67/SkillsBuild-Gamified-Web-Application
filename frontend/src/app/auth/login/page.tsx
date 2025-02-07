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

    const { isLoading, isError, message, fetchData } = useApi<
        LoginRequest,
        LoginRequest
    >("auth/login", { method: "POST", data: { username, password } });

    const isLoginSucessful = message === "Login successful";
    const isInvalidCredentials =
        message === "Invalid credentials" || message === "User not found";

    useEffect(() => {
        if (isLoginSucessful) {
            setIsAuthenticated(true);
            router.push("/");
        }
    }, [isLoginSucessful, router, setIsAuthenticated]);

    return (
        <div className="flex h-screen items-center justify-center">
            <div className="flex max-w-96 grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                <Logo />
                <h1 className="text-center font-bold md:text-2xl">Login</h1>

                <input
                    type="text"
                    placeholder="Username"
                    className={`rounded-lg border border-gray-300 px-2 py-1 md:py-2 ${
                        isInvalidCredentials && "border-red-500"
                    }`}
                    onChange={(e) => setUsername(e.target.value)}
                />

                <input
                    type="password"
                    placeholder="Password"
                    className={`rounded-lg border border-gray-300 px-2 py-1 md:py-2 ${
                        isInvalidCredentials && "border-red-500"
                    }`}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button
                    className="rounded-lg bg-blue-500 p-2 text-white disabled:bg-blue-300"
                    onClick={fetchData}
                    disabled={isLoading}
                >
                    {isLoading ? "Logging in..." : "Confirm"}
                </button>

                {isError && message && (
                    <div className="mb-2 text-sm text-red-500">{message}</div>
                )}

                <Link href="/auth/signup" className="text-center text-blue-500">
                    Don&apos;t have an account? Sign up here!
                </Link>
            </div>
        </div>
    );
}
