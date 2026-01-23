import { ThemeProvider } from '@/components/ThemeProvider';
import { LanguageProvider } from '@/components/LanguageProvider';
import { Toaster } from '@/components/ui/sonner';
import { Sidebar } from '@/components/Sidebar';
import { Header } from '@/components/Header';
import { AppRoutes } from '@/routes/AppRoutes';


export default function App() {

  return (
    <ThemeProvider>
      <LanguageProvider>
        <div className="flex h-screen bg-gray-50 dark:bg-gray-900">
          <Toaster richColors position="top-right" />
          <Sidebar />
          
          <div className="flex-1 flex flex-col overflow-hidden">
            <Header />
            
            <main className="flex-1 overflow-y-auto hide-scrollbar">
              <div className="px-7 py-6">
                <AppRoutes />
              </div>
            </main>
          </div>
        </div>
      </LanguageProvider>
    </ThemeProvider>
  );
}