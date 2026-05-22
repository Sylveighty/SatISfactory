## Summary

<!-- One or two sentences describing what this PR does. -->

## Related Kanban Task

<!-- Copy the task name from the board. Example: [feat] Add Student CRUD -->

## Type of Change

- [ ] New feature
- [ ] Bug fix
- [ ] Refactor
- [ ] UI / styling change
- [ ] Configuration / tooling
- [ ] Documentation

## Smoke Test Checklist

Run through these manually before requesting review.

- [ ] Application starts without errors (`./mvnw spring-boot:run`)
- [ ] Login works for all three roles (admin / faculty / student)
- [ ] Each role is redirected to the correct dashboard
- [ ] Accessing `/admin/**` as a student is blocked and redirects to login
- [ ] Logout clears the session and returns to the login page
- [ ] No browser console errors on pages affected by this PR
- [ ] New feature tested with happy path and at least one edge case

## Screenshots

<!-- If this PR changes any UI, paste a before/after screenshot. -->

## Notes for Reviewer

<!-- Anything that needs context, known limitations, or follow-up tasks. -->

