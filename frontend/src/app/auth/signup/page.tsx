"use client";

import { useApi } from "@/hooks/useApi";
import { useEffect, useState } from "react";
import { SignupRequest, SignupResponse } from "@/types/auth";
import Logo from "@/components/Logo";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/providers/AuthProvider";

export default function SignUpPage() {
    const { setIsAuthenticated } = useAuth();
    const router = useRouter();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const { isLoading, isError, message, fetchData } = useApi<
        SignupResponse,
        SignupRequest
    >("auth/signup", { method: "POST", data: { username, password } });

    const isSignupSucessful = message === "User created successfully";
    const isUserExists = message === "User already exists";

    useEffect(() => {
        let timeout: NodeJS.Timeout;
        if (isSignupSucessful) {
            setIsAuthenticated(true);
            timeout = setTimeout(() => router.push("/"), 3000);
        }

        return () => {
            if (timeout) clearTimeout(timeout);
        };
    }, [isSignupSucessful, router, setIsAuthenticated]);

    return (
        <div className="flex h-screen items-center justify-center">
            <div className="flex max-w-96 grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                {isSignupSucessful ? (
                    <div className="flex flex-col items-center gap-2">
                        <div>{message}</div>
                        <div>Redirecting to Home Page in 3 seconds...</div>
                    </div>
                ) : (
                    <>
                        <Logo />
                        <h1 className="text-center font-bold md:text-2xl">
                            Sign Up
                        </h1>

                        <input
                            type="text"
                            placeholder="Username"
                            className={`rounded-lg border border-gray-300 px-2 py-1 md:py-2 ${
                                isUserExists && "border-red-500"
                            }`}
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            className="rounded-lg border border-gray-300 px-2 py-1 md:py-2"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <button
                            className="rounded-lg bg-blue-500 p-2 text-white disabled:bg-blue-300"
                            onClick={() => fetchData()}
                            disabled={isLoading || isSignupSucessful}
                        >
                            {isLoading ? "Signing up..." : "Confirm"}
                        </button>
                        {isError && message && (
                            <div className="mb-2 grow text-center text-sm text-red-500">
                                {message}
                            </div>
                        )}
                        <Link
                            href="/auth/login"
                            className="text-center text-blue-500"
                        >
                            Already have an account? Login here!
                        </Link>
                    </>
                )}
            </div>
        </div>
    );
}
