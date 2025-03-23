"use client";

import { useState, useEffect } from "react";
import { useSignup } from "@/queries/auth/useSignup";
import Link from "next/link";
import { useRouter } from "next/navigation";
import Logo from "@/component/Logo";

export default function SignupPage() {
    const router = useRouter();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const mutation = useSignup();

    const handleSubmit = () => {
        mutation.mutate({ username, password });
    };

    useEffect(() => {
        if (mutation.isSuccess) {
            router.push("/");
        }
    }, [mutation.isSuccess, router]);

    return (
        <div className="flex h-screen items-center justify-center">
            <div className="flex max-w-96 grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                <Logo />
                <h1 className="text-center font-bold md:text-2xl">Signup</h1>

                <input
                    type="email"
                    placeholder="Email"
                    className={`rounded-lg border border-gray-300 px-2 py-1 md:py-2 ${
                        mutation.isError && "border-red-500"
                    }`}
                    onChange={(e) => setUsername(e.target.value)}
                />

                <input
                    type="password"
                    placeholder="Password"
                    className={`rounded-lg border border-gray-300 px-2 py-1 md:py-2 ${
                        mutation.isError && "border-red-500"
                    }`}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button
                    className="rounded-lg bg-blue-500 p-2 text-white disabled:bg-blue-300"
                    onClick={handleSubmit}
                    disabled={mutation.isPending}
                >
                    {mutation.isPending ? "Signing up..." : "Confirm"}
                </button>

                {mutation.isError && (
                    <div className="mb-2 text-sm text-red-500">{mutation.error.response?.data?.message}</div>
                )}

                <Link href="/auth/login" className="text-center text-blue-500">
                    Already have an account? Login here!
                </Link>
            </div>
        </div>
    );
};