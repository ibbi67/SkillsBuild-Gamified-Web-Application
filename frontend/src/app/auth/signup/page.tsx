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

    const { isLoading, isError, message, fetchData } = useApi<SignupResponse, SignupRequest>(
        "auth/signup",
        { method: "POST", data: { username, password } },
    );

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
        <div className="flex justify-center items-center h-screen">
            <div className="flex flex-col p-4 bg-white rounded-lg shadow-lg gap-4 grow max-w-96">
                {isSignupSucessful ? (
                    <div className="flex gap-2 items-center flex-col">
                        <div>{message}</div>
                        <div>Redirecting to Home Page in 3 seconds...</div>
                    </div>
                ) : (
                    <>
                        <Logo />
                        <h1 className="font-bold text-center md:text-2xl">Sign Up</h1>

                        <input
                            type="text"
                            placeholder="Username"
                            className={`py-1 px-2 md:py-2 border border-gray-300 rounded-lg ${
                                isUserExists && "border-red-500"
                            }`}
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            className="py-1 px-2 md:py-2 border border-gray-300 rounded-lg"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <button
                            className="bg-blue-500 text-white p-2 rounded-lg disabled:bg-blue-300"
                            onClick={fetchData}
                            disabled={isLoading || isSignupSucessful}
                        >
                            {isLoading ? "Signing up..." : "Confirm"}
                        </button>
                        {isError && message && (
                            <div className="text-red-500 text-sm mb-2 grow text-center">
                                {message}
                            </div>
                        )}
                        <Link href="/auth/login" className="text-blue-500 text-center">
                            Already have an account? Login here!
                        </Link>
                    </>
                )}
            </div>
        </div>
    );
}
