# Contact Parser - React Frontend

This is the React.js frontend for the Contact XML Parser application.

## Installation

First, install the dependencies:

```bash
npm install
```

## Development

To start the development server:

```bash
npm start
```

This will start the React app on `http://localhost:3000` (or the next available port).

The app is configured to proxy API requests to `http://localhost:8080` where the Spring Boot backend should be running.

## Building for Production

To build the app for production:

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

## Usage

1. Make sure the Spring Boot backend is running on port 8080
2. Start the React development server with `npm start`
3. Open your browser to the URL shown in the terminal (usually http://localhost:3000)
4. Use the tabs to switch between:
   - **File Path**: Enter a file path to parse
   - **Upload File**: Upload an XML file
   - **Paste XML**: Paste XML content directly
