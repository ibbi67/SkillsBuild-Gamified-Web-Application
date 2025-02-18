import { render, screen, waitFor } from "@testing-library/react";
import CoursePage from "../app/courses/[id]/page";
import { useParams } from "next/navigation";

// Mock course data
const mockCourse = {
  id: 1,
  title: "Project Management Fundamentals",
  description: "Learn to manage projects effectively",
  link: "http://example.com/project",
};

// Mock the API request
global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockCourse),
  })
) as jest.MockedFunction<any>;;

// Mock useParams to simulate Next.js dynamic routing
jest.mock("next/navigation", () => ({
  useParams: () => ({ id: "1" }),
}));

describe("CoursePage Component", () => {
  test("fetches and displays course details", async () => {
    render(<CoursePage />);

    await waitFor(() => {
      expect(screen.getByText("Project Management Fundamentals")).toBeInTheDocument();
      expect(screen.getByText("Learn to manage projects effectively")).toBeInTheDocument();
    });

    // Ensure course link is available
    const linkElement = screen.getByText("Go to Course");
    expect(linkElement).toBeInTheDocument();
    expect(linkElement).toHaveAttribute("href", "http://example.com/project");
  });
});
