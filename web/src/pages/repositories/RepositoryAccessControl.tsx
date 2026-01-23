import { useState } from 'react';
import { 
  Users, UserPlus, Key, Shield, Trash2, Edit, Search, ChevronDown, 
  Lock, Unlock, Copy, Check, Eye, EyeOff, Plus, X, Calendar
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';
import { copyToClipboard } from '@/utils/clipboard';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Switch } from '@/components/ui/switch';
import { Separator } from '@/components/ui/separator';

interface RepositoryAccessControlProps {
  repositoryId: string;
  repositoryName: string;
}

type Permission = 'admin' | 'write' | 'read';

interface UserPermission {
  id: string;
  name: string;
  email: string;
  role: Permission;
  addedAt: string;
  avatar?: string;
}

interface TeamPermission {
  id: string;
  name: string;
  memberCount: number;
  role: Permission;
  addedAt: string;
}

interface APIToken {
  id: string;
  name: string;
  token: string;
  permissions: Permission[];
  createdAt: string;
  lastUsed?: string;
  expiresAt?: string;
  isActive: boolean;
}

export function RepositoryAccessControl({ repositoryId, repositoryName }: RepositoryAccessControlProps) {
  const { t } = useLanguage();
  const [searchQuery, setSearchQuery] = useState('');
  const [isAddUserDialogOpen, setIsAddUserDialogOpen] = useState(false);
  const [isAddTeamDialogOpen, setIsAddTeamDialogOpen] = useState(false);
  const [isCreateTokenDialogOpen, setIsCreateTokenDialogOpen] = useState(false);
  const [newUserEmail, setNewUserEmail] = useState('');
  const [newUserRole, setNewUserRole] = useState<Permission>('read');
  const [newTeamId, setNewTeamId] = useState('');
  const [newTeamRole, setNewTeamRole] = useState<Permission>('read');
  const [newTokenName, setNewTokenName] = useState('');
  const [newTokenPermissions, setNewTokenPermissions] = useState<Permission[]>(['read']);
  const [newTokenExpires, setNewTokenExpires] = useState<string>('never');
  const [showToken, setShowToken] = useState<string | null>(null);
  const [copiedToken, setCopiedToken] = useState<string | null>(null);

  // Mock data - in real app, fetch from API
  const [userPermissions, setUserPermissions] = useState<UserPermission[]>([
    {
      id: '1',
      name: 'John Doe',
      email: 'john.doe@example.com',
      role: 'admin',
      addedAt: '2024-01-01',
    },
    {
      id: '2',
      name: 'Jane Smith',
      email: 'jane.smith@example.com',
      role: 'write',
      addedAt: '2024-01-05',
    },
    {
      id: '3',
      name: 'Bob Johnson',
      email: 'bob.johnson@example.com',
      role: 'read',
      addedAt: '2024-01-10',
    },
  ]);

  const [teamPermissions, setTeamPermissions] = useState<TeamPermission[]>([
    {
      id: '1',
      name: 'DevOps Team',
      memberCount: 8,
      role: 'admin',
      addedAt: '2024-01-01',
    },
    {
      id: '2',
      name: 'Backend Team',
      memberCount: 12,
      role: 'write',
      addedAt: '2024-01-03',
    },
    {
      id: '3',
      name: 'QA Team',
      memberCount: 5,
      role: 'read',
      addedAt: '2024-01-07',
    },
  ]);

  const [apiTokens, setApiTokens] = useState<APIToken[]>([
    {
      id: '1',
      name: 'CI/CD Pipeline',
      token: 'arp_1234567890abcdef',
      permissions: ['write', 'read'],
      createdAt: '2024-01-01',
      lastUsed: '2 hours ago',
      expiresAt: '2025-01-01',
      isActive: true,
    },
    {
      id: '2',
      name: 'Production Deploy',
      token: 'arp_abcdef1234567890',
      permissions: ['admin'],
      createdAt: '2024-01-05',
      lastUsed: '1 day ago',
      isActive: true,
    },
    {
      id: '3',
      name: 'Legacy System',
      token: 'arp_xyz9876543210abc',
      permissions: ['read'],
      createdAt: '2023-12-01',
      lastUsed: '30 days ago',
      expiresAt: '2024-06-01',
      isActive: false,
    },
  ]);

  const availableTeams = [
    { id: '4', name: 'Frontend Team', memberCount: 10 },
    { id: '5', name: 'Mobile Team', memberCount: 6 },
    { id: '6', name: 'Data Team', memberCount: 4 },
  ];

  const handleAddUser = () => {
    if (!newUserEmail) {
      toast.error(t('access.emailRequired'));
      return;
    }

    const newUser: UserPermission = {
      id: Date.now().toString(),
      name: newUserEmail.split('@')[0],
      email: newUserEmail,
      role: newUserRole,
      addedAt: new Date().toISOString().split('T')[0],
    };

    setUserPermissions([...userPermissions, newUser]);
    setIsAddUserDialogOpen(false);
    setNewUserEmail('');
    setNewUserRole('read');
    toast.success(t('access.userAdded'));
  };

  const handleRemoveUser = (userId: string) => {
    setUserPermissions(userPermissions.filter(u => u.id !== userId));
    toast.success(t('access.userRemoved'));
  };

  const handleUpdateUserRole = (userId: string, newRole: Permission) => {
    setUserPermissions(userPermissions.map(u => 
      u.id === userId ? { ...u, role: newRole } : u
    ));
    toast.success(t('access.permissionUpdated'));
  };

  const handleAddTeam = () => {
    if (!newTeamId) {
      toast.error(t('access.teamRequired'));
      return;
    }

    const team = availableTeams.find(t => t.id === newTeamId);
    if (!team) return;

    const newTeam: TeamPermission = {
      id: team.id,
      name: team.name,
      memberCount: team.memberCount,
      role: newTeamRole,
      addedAt: new Date().toISOString().split('T')[0],
    };

    setTeamPermissions([...teamPermissions, newTeam]);
    setIsAddTeamDialogOpen(false);
    setNewTeamId('');
    setNewTeamRole('read');
    toast.success(t('access.teamAdded'));
  };

  const handleRemoveTeam = (teamId: string) => {
    setTeamPermissions(teamPermissions.filter(t => t.id !== teamId));
    toast.success(t('access.teamRemoved'));
  };

  const handleUpdateTeamRole = (teamId: string, newRole: Permission) => {
    setTeamPermissions(teamPermissions.map(t => 
      t.id === teamId ? { ...t, role: newRole } : t
    ));
    toast.success(t('access.permissionUpdated'));
  };

  const handleCreateToken = () => {
    if (!newTokenName) {
      toast.error(t('access.tokenNameRequired'));
      return;
    }

    const token = `arp_${Math.random().toString(36).substring(2, 18)}`;
    const expiresAt = newTokenExpires === 'never' ? undefined : 
      newTokenExpires === '30d' ? new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0] :
      newTokenExpires === '90d' ? new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0] :
      new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

    const newToken: APIToken = {
      id: Date.now().toString(),
      name: newTokenName,
      token,
      permissions: newTokenPermissions,
      createdAt: new Date().toISOString().split('T')[0],
      expiresAt,
      isActive: true,
    };

    setApiTokens([newToken, ...apiTokens]);
    setShowToken(token);
    setNewTokenName('');
    setNewTokenPermissions(['read']);
    setNewTokenExpires('never');
    toast.success(t('access.tokenCreated'));
  };

  const handleCopyToken = async (token: string, tokenId: string) => {
    const success = await copyToClipboard(token);
    if (success) {
      setCopiedToken(tokenId);
      toast.success(t('access.tokenCopied'));
      setTimeout(() => setCopiedToken(null), 2000);
    }
  };

  const handleRevokeToken = (tokenId: string) => {
    setApiTokens(apiTokens.map(t => 
      t.id === tokenId ? { ...t, isActive: false } : t
    ));
    toast.success(t('access.tokenRevoked'));
  };

  const handleActivateToken = (tokenId: string) => {
    setApiTokens(apiTokens.map(t => 
      t.id === tokenId ? { ...t, isActive: true } : t
    ));
    toast.success(t('access.tokenActivated'));
  };

  const handleDeleteToken = (tokenId: string) => {
    setApiTokens(apiTokens.filter(t => t.id !== tokenId));
    toast.success(t('access.tokenDeleted'));
  };

  const getRoleBadgeColor = (role: Permission) => {
    if (role === 'admin') return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300';
    if (role === 'write') return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300';
    return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const getRoleIcon = (role: Permission) => {
    if (role === 'admin') return <Shield className="size-3 mr-1" />;
    if (role === 'write') return <Edit className="size-3 mr-1" />;
    return <Eye className="size-3 mr-1" />;
  };

  const getInitials = (name: string) => {
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-semibold text-gray-900 dark:text-white">{t('access.title')}</h2>
        <p className="text-gray-600 dark:text-gray-400 mt-1">
          {t('access.description')} {repositoryName}
        </p>
      </div>

      <Tabs defaultValue="users" className="space-y-6">
        <TabsList>
          <TabsTrigger value="users">
            <Users className="mr-2 size-4" />
            {t('access.users')} ({userPermissions.length})
          </TabsTrigger>
          <TabsTrigger value="teams">
            <Users className="mr-2 size-4" />
            {t('access.teams')} ({teamPermissions.length})
          </TabsTrigger>
          <TabsTrigger value="tokens">
            <Key className="mr-2 size-4" />
            {t('access.apiTokens')} ({apiTokens.filter(t => t.isActive).length})
          </TabsTrigger>
        </TabsList>

        {/* Users Tab */}
        <TabsContent value="users" className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex-1 max-w-md relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder={t('access.searchUsers')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Button onClick={() => setIsAddUserDialogOpen(true)}>
              <UserPlus className="mr-2 size-4" />
              {t('access.addUser')}
            </Button>
          </div>

          <Card>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{t('access.user')}</TableHead>
                  <TableHead>{t('access.email')}</TableHead>
                  <TableHead>{t('access.role')}</TableHead>
                  <TableHead>{t('access.addedAt')}</TableHead>
                  <TableHead className="text-right">{t('common.actions')}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {userPermissions.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell>
                      <div className="flex items-center gap-3">
                        <div className="size-8 bg-blue-100 dark:bg-blue-900/30 rounded-full flex items-center justify-center text-xs font-medium text-blue-600 dark:text-blue-400">
                          {getInitials(user.name)}
                        </div>
                        <span className="font-medium text-gray-900 dark:text-white">{user.name}</span>
                      </div>
                    </TableCell>
                    <TableCell className="text-gray-600 dark:text-gray-400">{user.email}</TableCell>
                    <TableCell>
                      <Select
                        value={user.role}
                        onValueChange={(value) => handleUpdateUserRole(user.id, value as Permission)}
                      >
                        <SelectTrigger className="w-32">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="admin">
                            <div className="flex items-center">
                              <Shield className="size-3 mr-2" />
                              {t('access.admin')}
                            </div>
                          </SelectItem>
                          <SelectItem value="write">
                            <div className="flex items-center">
                              <Edit className="size-3 mr-2" />
                              {t('access.write')}
                            </div>
                          </SelectItem>
                          <SelectItem value="read">
                            <div className="flex items-center">
                              <Eye className="size-3 mr-2" />
                              {t('access.read')}
                            </div>
                          </SelectItem>
                        </SelectContent>
                      </Select>
                    </TableCell>
                    <TableCell className="text-gray-600 dark:text-gray-400">{user.addedAt}</TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleRemoveUser(user.id)}
                      >
                        <Trash2 className="size-4 text-red-600" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>
        </TabsContent>

        {/* Teams Tab */}
        <TabsContent value="teams" className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex-1 max-w-md relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder={t('access.searchTeams')}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Button onClick={() => setIsAddTeamDialogOpen(true)}>
              <Plus className="mr-2 size-4" />
              {t('access.addTeam')}
            </Button>
          </div>

          <Card>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{t('access.team')}</TableHead>
                  <TableHead>{t('access.members')}</TableHead>
                  <TableHead>{t('access.role')}</TableHead>
                  <TableHead>{t('access.addedAt')}</TableHead>
                  <TableHead className="text-right">{t('common.actions')}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {teamPermissions.map((team) => (
                  <TableRow key={team.id}>
                    <TableCell>
                      <div className="flex items-center gap-3">
                        <div className="size-8 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                          <Users className="size-4 text-purple-600 dark:text-purple-400" />
                        </div>
                        <span className="font-medium text-gray-900 dark:text-white">{team.name}</span>
                      </div>
                    </TableCell>
                    <TableCell className="text-gray-600 dark:text-gray-400">
                      {team.memberCount} {t('access.members').toLowerCase()}
                    </TableCell>
                    <TableCell>
                      <Select
                        value={team.role}
                        onValueChange={(value) => handleUpdateTeamRole(team.id, value as Permission)}
                      >
                        <SelectTrigger className="w-32">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="admin">
                            <div className="flex items-center">
                              <Shield className="size-3 mr-2" />
                              {t('access.admin')}
                            </div>
                          </SelectItem>
                          <SelectItem value="write">
                            <div className="flex items-center">
                              <Edit className="size-3 mr-2" />
                              {t('access.write')}
                            </div>
                          </SelectItem>
                          <SelectItem value="read">
                            <div className="flex items-center">
                              <Eye className="size-3 mr-2" />
                              {t('access.read')}
                            </div>
                          </SelectItem>
                        </SelectContent>
                      </Select>
                    </TableCell>
                    <TableCell className="text-gray-600 dark:text-gray-400">{team.addedAt}</TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleRemoveTeam(team.id)}
                      >
                        <Trash2 className="size-4 text-red-600" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>
        </TabsContent>

        {/* API Tokens Tab */}
        <TabsContent value="tokens" className="space-y-4">
          <div className="flex items-center justify-between">
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {t('access.tokenDescription')}
            </p>
            <Button onClick={() => setIsCreateTokenDialogOpen(true)}>
              <Plus className="mr-2 size-4" />
              {t('access.createToken')}
            </Button>
          </div>

          <div className="grid gap-4">
            {apiTokens.map((token) => (
              <Card key={token.id}>
                <CardContent className="pt-6">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <h3 className="font-medium text-gray-900 dark:text-white">{token.name}</h3>
                        <Badge variant={token.isActive ? 'default' : 'secondary'}>
                          {token.isActive ? t('access.active') : t('access.inactive')}
                        </Badge>
                        {token.permissions.map(perm => (
                          <Badge key={perm} className={getRoleBadgeColor(perm)}>
                            {getRoleIcon(perm)}
                            {t(`access.${perm}`)}
                          </Badge>
                        ))}
                      </div>
                      <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                        <div className="flex items-center gap-1">
                          <Calendar className="size-3" />
                          <span>{t('access.created')}: {token.createdAt}</span>
                        </div>
                        {token.lastUsed && (
                          <div>
                            <span>{t('access.lastUsed')}: {token.lastUsed}</span>
                          </div>
                        )}
                        {token.expiresAt && (
                          <div>
                            <span>{t('access.expires')}: {token.expiresAt}</span>
                          </div>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      {token.isActive ? (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleRevokeToken(token.id)}
                        >
                          <Lock className="mr-2 size-4" />
                          {t('access.revoke')}
                        </Button>
                      ) : (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleActivateToken(token.id)}
                        >
                          <Unlock className="mr-2 size-4" />
                          {t('access.activate')}
                        </Button>
                      )}
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDeleteToken(token.id)}
                      >
                        <Trash2 className="size-4 text-red-600" />
                      </Button>
                    </div>
                  </div>

                  <div className="bg-gray-50 dark:bg-gray-800/50 rounded-lg p-3 flex items-center justify-between">
                    <code className="text-sm font-mono text-gray-900 dark:text-white">
                      {token.token}
                    </code>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleCopyToken(token.token, token.id)}
                    >
                      {copiedToken === token.id ? (
                        <>
                          <Check className="mr-2 size-4" />
                          {t('access.copied')}
                        </>
                      ) : (
                        <>
                          <Copy className="mr-2 size-4" />
                          {t('access.copy')}
                        </>
                      )}
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>
      </Tabs>

      {/* Add User Dialog */}
      <Dialog open={isAddUserDialogOpen} onOpenChange={setIsAddUserDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{t('access.addUser')}</DialogTitle>
            <DialogDescription>{t('access.addUserDescription')}</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="user-email">{t('access.email')}</Label>
              <Input
                id="user-email"
                placeholder="user@example.com"
                value={newUserEmail}
                onChange={(e) => setNewUserEmail(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="user-role">{t('access.role')}</Label>
              <Select value={newUserRole} onValueChange={(value) => setNewUserRole(value as Permission)}>
                <SelectTrigger id="user-role">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="read">
                    <div className="flex items-center">
                      <Eye className="size-3 mr-2" />
                      {t('access.read')} - {t('access.readDesc')}
                    </div>
                  </SelectItem>
                  <SelectItem value="write">
                    <div className="flex items-center">
                      <Edit className="size-3 mr-2" />
                      {t('access.write')} - {t('access.writeDesc')}
                    </div>
                  </SelectItem>
                  <SelectItem value="admin">
                    <div className="flex items-center">
                      <Shield className="size-3 mr-2" />
                      {t('access.admin')} - {t('access.adminDesc')}
                    </div>
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsAddUserDialogOpen(false)}>
              {t('common.cancel')}
            </Button>
            <Button onClick={handleAddUser}>
              {t('access.addUser')}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Add Team Dialog */}
      <Dialog open={isAddTeamDialogOpen} onOpenChange={setIsAddTeamDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{t('access.addTeam')}</DialogTitle>
            <DialogDescription>{t('access.addTeamDescription')}</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="team-select">{t('access.selectTeam')}</Label>
              <Select value={newTeamId} onValueChange={setNewTeamId}>
                <SelectTrigger id="team-select">
                  <SelectValue placeholder={t('access.selectTeamPlaceholder')} />
                </SelectTrigger>
                <SelectContent>
                  {availableTeams.filter(t => !teamPermissions.find(tp => tp.id === t.id)).map(team => (
                    <SelectItem key={team.id} value={team.id}>
                      {team.name} ({team.memberCount} {t('access.members').toLowerCase()})
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="team-role">{t('access.role')}</Label>
              <Select value={newTeamRole} onValueChange={(value) => setNewTeamRole(value as Permission)}>
                <SelectTrigger id="team-role">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="read">
                    <div className="flex items-center">
                      <Eye className="size-3 mr-2" />
                      {t('access.read')} - {t('access.readDesc')}
                    </div>
                  </SelectItem>
                  <SelectItem value="write">
                    <div className="flex items-center">
                      <Edit className="size-3 mr-2" />
                      {t('access.write')} - {t('access.writeDesc')}
                    </div>
                  </SelectItem>
                  <SelectItem value="admin">
                    <div className="flex items-center">
                      <Shield className="size-3 mr-2" />
                      {t('access.admin')} - {t('access.adminDesc')}
                    </div>
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsAddTeamDialogOpen(false)}>
              {t('common.cancel')}
            </Button>
            <Button onClick={handleAddTeam}>
              {t('access.addTeam')}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Create Token Dialog */}
      <Dialog open={isCreateTokenDialogOpen || showToken !== null} onOpenChange={(open) => {
        setIsCreateTokenDialogOpen(open);
        if (!open) setShowToken(null);
      }}>
        <DialogContent>
          {showToken ? (
            <>
              <DialogHeader>
                <DialogTitle>{t('access.tokenCreated')}</DialogTitle>
                <DialogDescription>{t('access.tokenCreatedDesc')}</DialogDescription>
              </DialogHeader>
              <div className="py-4">
                <div className="bg-yellow-50 dark:bg-yellow-950/20 border border-yellow-200 dark:border-yellow-900 rounded-lg p-4 mb-4">
                  <p className="text-sm text-yellow-800 dark:text-yellow-200">
                    {t('access.tokenWarning')}
                  </p>
                </div>
                <div className="bg-gray-50 dark:bg-gray-800/50 rounded-lg p-4">
                  <Label className="text-xs text-gray-600 dark:text-gray-400 mb-2 block">
                    {t('access.yourToken')}
                  </Label>
                  <div className="flex items-center gap-2">
                    <code className="flex-1 text-sm font-mono text-gray-900 dark:text-white break-all">
                      {showToken}
                    </code>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleCopyToken(showToken, 'new')}
                    >
                      {copiedToken === 'new' ? (
                        <Check className="size-4" />
                      ) : (
                        <Copy className="size-4" />
                      )}
                    </Button>
                  </div>
                </div>
              </div>
              <DialogFooter>
                <Button onClick={() => {
                  setShowToken(null);
                  setIsCreateTokenDialogOpen(false);
                }}>
                  {t('access.gotIt')}
                </Button>
              </DialogFooter>
            </>
          ) : (
            <>
              <DialogHeader>
                <DialogTitle>{t('access.createToken')}</DialogTitle>
                <DialogDescription>{t('access.createTokenDescription')}</DialogDescription>
              </DialogHeader>
              <div className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="token-name">{t('access.tokenName')}</Label>
                  <Input
                    id="token-name"
                    placeholder="e.g., CI/CD Pipeline"
                    value={newTokenName}
                    onChange={(e) => setNewTokenName(e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label>{t('access.permissions')}</Label>
                  <div className="space-y-2">
                    {(['read', 'write', 'admin'] as Permission[]).map(perm => (
                      <div key={perm} className="flex items-center justify-between p-3 border border-gray-200 dark:border-gray-700 rounded-lg">
                        <div className="flex items-center gap-2">
                          {getRoleIcon(perm)}
                          <div>
                            <p className="font-medium text-gray-900 dark:text-white">{t(`access.${perm}`)}</p>
                            <p className="text-xs text-gray-600 dark:text-gray-400">{t(`access.${perm}Desc`)}</p>
                          </div>
                        </div>
                        <Switch
                          checked={newTokenPermissions.includes(perm)}
                          onCheckedChange={(checked) => {
                            if (checked) {
                              setNewTokenPermissions([...newTokenPermissions, perm]);
                            } else {
                              setNewTokenPermissions(newTokenPermissions.filter(p => p !== perm));
                            }
                          }}
                        />
                      </div>
                    ))}
                  </div>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="token-expires">{t('access.expiration')}</Label>
                  <Select value={newTokenExpires} onValueChange={setNewTokenExpires}>
                    <SelectTrigger id="token-expires">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="30d">{t('access.expires30d')}</SelectItem>
                      <SelectItem value="90d">{t('access.expires90d')}</SelectItem>
                      <SelectItem value="1y">{t('access.expires1y')}</SelectItem>
                      <SelectItem value="never">{t('access.neverExpires')}</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsCreateTokenDialogOpen(false)}>
                  {t('common.cancel')}
                </Button>
                <Button onClick={handleCreateToken} disabled={newTokenPermissions.length === 0}>
                  {t('access.createToken')}
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
