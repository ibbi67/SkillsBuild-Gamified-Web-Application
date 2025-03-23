"use client"

import { useEffect, useState } from "react";
import { useLogin } from "@/queries/auth/useLogin";
import Link from "next/link";
import { useRouter } from "next/navigation";
import Logo from "@/component/Logo";

export default function LoginPage() {
    const router = useRouter();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const mutation = useLogin();

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
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
                <h1 className="text-center font-bold md:text-2xl">Login</h1>

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
                    {mutation.isPending ? "Logging in..." : "Login"}
                </button>

                {mutation.isError && (
                    <div className="mb-2 text-sm text-red-500">{mutation.error.response?.data.message}</div>
                )}

                <Link href="/auth/signup" className="text-center text-blue-500">
                    Don't have an account? Signup here!
                </Link>
            </div>
        </div>
    );
};