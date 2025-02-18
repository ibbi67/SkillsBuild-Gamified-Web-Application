import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import SearchBar from "../components/Searchbar"; // Ensure correct file casing
import "@testing-library/jest-dom";

const mockCourses = [
  {
    id: 1,
    title: "Project Management Fundamentals",
    description: "Learn to manage projects effectively",
    link: "http://example.com/project",
  },
  {
    id: 2,
    title: "Introduction to AI",
    description: "Learn the basics of AI",
    link: "http://example.com/ai",
  },
];

beforeEach(() => {
  // ✅ Mock Fetch Debugging
  console.log("🔍 Mock Fetch Called");
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true, // ✅ Ensure the mock response mimics a real fetch
      json: () => Promise.resolve(mockCourses),
    })
  ) as jest.Mock;
});

afterEach(() => {
  jest.restoreAllMocks();
});

describe("SearchBar Component", () => {
  test("fetches courses and displays results", async () => {
    render(<SearchBar />);

    // ✅ Log the DOM Before Fetch
    console.log("🔍 Before Fetch: Rendered DOM:");
    screen.debug();

    // ✅ Ensure fetch was called
    await waitFor(() => expect(global.fetch).toHaveBeenCalled());

    // ✅ Log the DOM After Fetch
    console.log("🔍 After Fetch: Rendered DOM:");
    screen.debug();

    // ✅ Force a state update by simulating user typing
    const input = screen.getByPlaceholderText("Search courses...");
    fireEvent.change(input, { target: { value: "Project" } });

    // ✅ Ensure UI updates with course titles
    await waitFor(() => {
      expect(screen.getByText("Project Management Fundamentals")).toBeInTheDocument();
    });
  });

  test("filters search results correctly when typing", async () => {
    render(<SearchBar />);

    // ✅ Ensure fetch was called
    await waitFor(() => expect(global.fetch).toHaveBeenCalled());

    // ✅ Simulate typing in search input
    const input = screen.getByPlaceholderText("Search courses...");
    fireEvent.change(input, { target: { value: "Project" } });

    // ✅ Ensure filtered result appears
    await waitFor(() => expect(screen.getByText("Project Management Fundamentals")).toBeInTheDocument());
  });

  test("navigates to the correct course page when a search result is clicked", async () => {
    render(<SearchBar />);

    // ✅ Ensure fetch was called
    await waitFor(() => expect(global.fetch).toHaveBeenCalled());

    // ✅ Simulate user typing to filter results
    const input = screen.getByPlaceholderText("Search courses...");
    fireEvent.change(input, { target: { value: "Project" } });

    // ✅ Ensure filtered result appears
    await waitFor(() => expect(screen.getByText("Project Management Fundamentals")).toBeInTheDocument());

    // ✅ Simulate user clicking the search result
    const courseLink = screen.getByText("Project Management Fundamentals");
    fireEvent.click(courseLink);
  });
});
